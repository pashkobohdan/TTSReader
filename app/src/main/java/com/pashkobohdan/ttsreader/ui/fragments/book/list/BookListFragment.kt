package com.pashkobohdan.ttsreader.ui.fragments.book.list

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.github.clans.fab.FloatingActionMenu
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.TTSReaderApplication
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookList.BookListPresenter
import com.pashkobohdan.ttsreader.mvp.bookList.view.BookListView
import com.pashkobohdan.ttsreader.ui.ActivityStartable
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder
import com.pashkobohdan.ttsreader.ui.dialog.BookEditDialog
import com.pashkobohdan.ttsreader.ui.dialog.DialogUtils
import com.pashkobohdan.ttsreader.ui.fragments.book.list.widget.BookListItemWidget
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractListFragment
import com.pashkobohdan.ttsreader.ui.listener.UpDownScrollListener
import com.pashkobohdan.ttsreader.utils.Constants
import com.pashkobohdan.ttsreader.utils.enums.BookActionType
import com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpeningThread.FileOpenThread
import ir.sohreco.androidfilechooser.FileChooserDialog
import rx.functions.Action1
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

open class BookListFragment : AbstractListFragment<BookListPresenter, BookDTO>(), BookListView {

    private val OPEN_PDF_FB2_TXT_FILE = 1000

    @InjectPresenter
    lateinit var presenter: BookListPresenter
    @Inject
    lateinit var bookListItemWidgetProvider: Provider<BookListItemWidget>
    @Inject
    lateinit var activityStartable: ActivityStartable

    @BindView(R.id.book_list_recycler_view)
    lateinit var bookListRecyclerView: RecyclerView
    @BindView(R.id.fragment_book_list_waiter_mask)
    lateinit var waiterProgressBar: ProgressBar
    @BindView(R.id.add_book_fab_menu)
    lateinit var addBookActionMenu: FloatingActionMenu
    @BindView(R.id.bookListContainer)
    lateinit var bookListContainer: View
    @BindView(R.id.noDataContainer)
    lateinit var emptyBookListContainer: View

    private lateinit var adapter: ListAdapter

    val samplePresenter: BookListPresenter
        @ProvidePresenter
        get() = presenterProvider.get()

    @OnClick(R.id.add_book_from_file_fab)
    internal fun addBookFromFileButtonClicked() {
        addBookActionMenu.close(true)
        presenter.openNewBook()
    }

    @OnClick(R.id.add_book_create_fab)
    internal fun createBookButtonClicked() {
        addBookActionMenu.close(true)
        showCreateNewBookDialog()
    }

    @OnClick(R.id.add_book_download_fab)
    internal fun downloadBookButtonClicked() {
        addBookActionMenu.close(true)
        presenter.downloadFromCloud()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TTSReaderApplication.INSTANCE.getApplicationComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addLeftHeaderView(createEmptyHeaderButton())
        setRightHeaderView(createImageHeaderButton(R.drawable.refresh, {
            presenter.refresh()
        }))

        adapter = ListAdapter(ArrayList<BookDTO>())

        val mLayoutManager = LinearLayoutManager(context)
        bookListRecyclerView.layoutManager = mLayoutManager
        bookListRecyclerView.adapter = adapter

        bookListRecyclerView.addOnScrollListener(object : UpDownScrollListener() {

            override fun scrollDown() {
                addBookActionMenu.hideMenu(true)
            }

            override fun scrollUp() {
                addBookActionMenu.showMenu(true)
            }
        })

        setHeaderTitle(getString(R.string.book_list_header))
    }

    override fun showFileOpenerChooser() {
        DialogUtils.showAlert(getString(R.string.file_opener), getString(R.string.choose_file_manage)
                , getString(R.string.custom_file_manager)
                , getString(R.string.system_file_manager), context!!,
                this::openCustomFileManager, this::openSystemFileManager)
    }

    private fun openCustomFileManager() {
        val openFileBuilder = FileChooserDialog.Builder(FileChooserDialog.ChooserType.FILE_CHOOSER,
                FileChooserDialog.ChooserListener {
                    presenter.openNewBookSelected(File(it))
                })
                .setTitle(getString(R.string.select_file))
                .setFileFormats(arrayOf(Constants.TXT_FORMAT, Constants.PDF_FORMAT,
                        Constants.FB2_FORMAT, Constants.EPUB_FORMAT))

        openFileBuilder.build().show(fragmentManager, null)
    }

    private fun openSystemFileManager() {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "file/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            try {
                activityStartable.startActivityForResult(OPEN_PDF_FB2_TXT_FILE,
                        Intent.createChooser(intent, getString(R.string.choose_file_of_setted_types)),
                        { intent, resultCode ->
                            if (resultCode == Activity.RESULT_OK && intent != null) {
                                val myFile = File(getRealPathFromURI(intent.getData()))

                                if (myFile.getAbsolutePath().endsWith("pdf") ||
                                        myFile.getAbsolutePath().endsWith("fb2") ||
                                        myFile.getAbsolutePath().endsWith("txt") ||
                                        myFile.getAbsolutePath().endsWith("epub")) {

                                    presenter.openNewBookSelected(myFile)
                                } else {
                                    AlertDialog.Builder(context)
                                            .setTitle(getString(R.string.error))
                                            .setMessage(getString(R.string.choose_correct_file))
                                            .create()
                                            .show()
                                }

                            } else {
                                AlertDialog.Builder(context)
                                        .setTitle(getString(R.string.error))
                                        .setMessage(getString(R.string.choose_correct_file))
                                        .create()
                                        .show()
                            }
                        })
            } catch (e: ActivityNotFoundException) {
                throw e
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun showBookOpenDialog(file: File) {
        val pd = ProgressDialog(context)
        pd.setTitle(getString(R.string.reading))
        pd.setMessage(getString(R.string.book_reading_waiter))
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pd.max = 100
        pd.progress = 0
        pd.isIndeterminate = true
        pd.setCancelable(false)
        pd.show()

        FileOpenThread(file, activity!!, { o, n ->
            pd.isIndeterminate = false
            pd.progress = n
        }, {
            pd.dismiss()
        }, presenter::bookReadingEnd).start()
    }

    private fun getRealPathFromURI(contentURI: Uri?): String {
        val result: String
        val cursor = context?.contentResolver?.query(contentURI!!, null, null, null, null)
        if (cursor == null) {
            result = contentURI?.path ?: Constants.EMPTY
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

    override fun showBookList(bookDTOList: List<BookDTO>) {
        if (bookDTOList.isEmpty()) {
            emptyBookListContainer.visibility = View.VISIBLE
            bookListContainer.visibility = View.GONE
        } else {
            emptyBookListContainer.visibility = View.GONE
            bookListContainer.visibility = View.VISIBLE
            adapter.setDataList(bookDTOList)
        }
    }

    override fun showEditBook(bookDTO: BookDTO) {
        val dialog = BookEditDialog(context!!, bookDTO, presenter::saveBook)
        dialog.show()
    }

    private fun showCreateNewBookDialog() {
        val dialog = BookEditDialog(context!!, null, presenter::saveBook)
        dialog.show()
    }

    override fun showGetBookListError() {
        DialogUtils.showAlert(getString(R.string.error), getString(R.string.books_reading_error), context
                ?: throw IllegalStateException("Context is null"), { })
    }

    override fun showBookIsAlreadyExistError() {
        DialogUtils.showAlert(getString(R.string.error), getString(R.string.same_book_is_exist), context
                ?: throw IllegalStateException("Context is null"), { })
    }

    override fun showProgress() {
        waiterProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        waiterProgressBar.visibility = View.GONE
    }

    override fun getItemHolder(parent: ViewGroup): AbstractListItemHolder<BookDTO> {
        return bookListItemWidgetProvider.get().getHolder(parent, Action1 { book -> tryOpenBook(book) }
                , Action1 { bookDTO ->
            DialogUtils.showOptionsDialog(getString(R.string.choose_action_type), context!!, Arrays.asList(*BookActionType.values()),
                    { action ->
                        when (action) {
                            BookActionType.DELETE -> getString(R.string.delete)
                            BookActionType.EDIT -> getString(R.string.edit)
                            BookActionType.OPEN -> getString(R.string.open)
                            BookActionType.UPLOAD_TO_PUBLIC_CLOUD -> getString(R.string.upload_to_cloud)
                            else -> Constants.EMPTY
                        }
                    }, { action ->
                when (action) {
                    BookActionType.DELETE -> presenter.deleteBook(bookDTO)
                    BookActionType.EDIT -> presenter.editBook(bookDTO)
                    BookActionType.OPEN -> tryOpenBook(bookDTO)
                    BookActionType.UPLOAD_TO_PUBLIC_CLOUD -> presenter.uploadToStorage(bookDTO)
                    else -> throw IllegalArgumentException("Unsupported book action: " + action.name)
                }
            }, true)
        })
    }

    open fun tryOpenBook(book: BookDTO) {
        presenter.openBook(book)
    }

    override fun bookSaveSuccess() {
        Snackbar.make(addBookActionMenu, getString(R.string.book_save_success_message),
                Snackbar.LENGTH_LONG).show()
    }

    override fun bookSaveError(bookDTO: BookDTO) {
        Snackbar.make(addBookActionMenu, getString(R.string.book_saving_error_message),
                Snackbar.LENGTH_LONG).setAction(getString(R.string.try_again)) { presenter::saveBook }.show()
    }

    override fun bookRemoveSuccess() {
        Snackbar.make(addBookActionMenu, getString(R.string.book_delete_success_message),
                Snackbar.LENGTH_LONG).show()
    }

    override fun bookRemoveError(bookDTO: BookDTO) {
        Snackbar.make(addBookActionMenu, getString(R.string.book_delete_error_message),
                Snackbar.LENGTH_LONG).setAction(getString(R.string.try_again)) { presenter::deleteBook }.show()
    }

    override fun bookUploadError(bookDTO: BookDTO) {
        Snackbar.make(addBookActionMenu, getString(R.string.book_upload_error_message),
                Snackbar.LENGTH_LONG).setAction(getString(R.string.try_again)) { presenter.uploadToStorage(bookDTO) }.show()
    }

    override fun bookUploadSuccess() {
        Snackbar.make(addBookActionMenu, getString(R.string.book_upload_success_message),
                Snackbar.LENGTH_LONG).show()
    }
}
