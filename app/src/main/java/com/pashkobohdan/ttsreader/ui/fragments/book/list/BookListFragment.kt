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
import com.pashkobohdan.ttsreader.TTSReaderProApplication
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookList.BookListPresenter
import com.pashkobohdan.ttsreader.mvp.bookList.view.BookListView
import com.pashkobohdan.ttsreader.ui.ActivityStartable
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder
import com.pashkobohdan.ttsreader.ui.dialog.BookEditDialog
import com.pashkobohdan.ttsreader.ui.dialog.DialogUtils
import com.pashkobohdan.ttsreader.ui.fragments.book.widget.BookListItemWidget
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractListFragment
import com.pashkobohdan.ttsreader.ui.listener.UpDownScrollListener
import com.pashkobohdan.ttsreader.utils.Constants
import com.pashkobohdan.ttsreader.utils.enums.BookActionType
import com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpeningThread.FileOpenThread
import ir.sohreco.androidfilechooser.FileChooserDialog
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class BookListFragment : AbstractListFragment<BookListPresenter, BookDTO>(), BookListView {

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
        //TODO open download book dialog/fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TTSReaderProApplication.INSTANCE.applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        //        setRetainInstance(true);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //        setHeaderTitle("Book list fragment");

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
    }

    override fun showFileOpenerChooser() {
        DialogUtils.showAlert("File open", "Choose file manager",
                "Custom (app) file manager", "System file manager", context!!,
                this::openCustomFileManager, this::openSystemFileManager)
    }

    private fun openCustomFileManager() {
        val openFileBuilder = FileChooserDialog.Builder(FileChooserDialog.ChooserType.FILE_CHOOSER,
                FileChooserDialog.ChooserListener {
                    presenter.openNewBookSelected(File(it))
                })
                .setTitle("Select a file")
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
                        Intent.createChooser(intent, "Выберите pdf, txt, fb2 файл"),
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
                                            .setTitle("Error")
                                            .setMessage("Choose correct file")
                                            .create()
                                            .show()
                                }

                            } else {
                                AlertDialog.Builder(context)
                                        .setTitle("Error")
                                        .setMessage("Choose correct file")
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

    private fun selectOpeningFile(inputFile: File) {

//        val bookName = InternalStorageFileHelper.fileNameWithoutExtension(inputFile)

//        if (InternalStorageFileHelper.isFileWasOpened(this, inputFile)) {
//
//            val dialogClickListener = { dialog, which ->
//                when (which) {
//                    DialogInterface.BUTTON_POSITIVE -> openFileWithUI(inputFile)
//
//                    DialogInterface.BUTTON_NEGATIVE -> {
//                    }
//                }
//            }
//
//            val builder = AlertDialog.Builder(context)
//            builder.setMessage(getString(R.string.book_rewriting_confirm) + "\"" +
//                    bookName +
//                    "\"")
//                    .setPositiveButton("Yes", dialogClickListener)
//                    .setNegativeButton("No", dialogClickListener)
//                    .show()
//
//        } else {
//        openFileWithUI(inputFile)
//        }
    }

    override fun showBookOpenDialog(file: File) {
        val pd = ProgressDialog(context)
        pd.setTitle("Reading")
        pd.setMessage("Please, wait while book is loading")
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pd.max = 100
        pd.progress = 0
        pd.isIndeterminate = true
        pd.setCancelable(false)
        pd.show()

        //TODO to presenter
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
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI?.path ?: Constants.EMPTY
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return result
    }

//    private fun selectOpeningFile(inputFile: File) {
//        val bookName = InternalStorageFileHelper.fileNameWithoutExtension(inputFile)
//
//        if (InternalStorageFileHelper.isFileWasOpened(this, inputFile)) {
//
//            val dialogClickListener = { dialog, which ->
//                when (which) {
//                    DialogInterface.BUTTON_POSITIVE -> openFileWithUI(inputFile)
//
//                    DialogInterface.BUTTON_NEGATIVE -> {
//                    }
//                }
//            }
//
//            val builder = AlertDialog.Builder(this)
//            builder.setMessage(getString(R.string.book_rewriting_confirm) + "\"" +
//                    bookName +
//                    "\"")
//                    .setPositiveButton(R.string.yes, dialogClickListener)
//                    .setNegativeButton(R.string.no, dialogClickListener)
//                    .show()
//
//        } else {
//            openFileWithUI(inputFile)
//        }
//    }

    override fun showBookList(bookDTOList: List<BookDTO>) {
        adapter.setDataList(bookDTOList)
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
        DialogUtils.showAlert("Error", "Data execution error, try later", context
                ?: throw IllegalStateException("Context is null"), { })
    }

    override fun showBookIsAlreadyExistError() {
        DialogUtils.showAlert("Error", "Book with it's name is already exist. Try later", context
                ?: throw IllegalStateException("Context is null"), { })
    }

    override fun showProgress() {
        waiterProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        waiterProgressBar.visibility = View.GONE
    }

    override fun getItemHolder(parent: ViewGroup): AbstractListItemHolder<BookDTO> {
        return bookListItemWidgetProvider.get().getHolder(parent, { book -> presenter.openBook(book) }
        ) { bookDTO ->
            DialogUtils.showOptionsDialog("Choose action type", context!!, Arrays.asList(*BookActionType.values()),
                    { action ->
                        when (action) {
                            BookActionType.DELETE -> "Delete"
                            BookActionType.EDIT -> "Edit"
                            BookActionType.OPEN -> "Open"
                            else -> Constants.EMPTY
                        }
                    }, { action ->
                when (action) {
                    BookActionType.DELETE -> presenter.deleteBook(bookDTO)
                    BookActionType.EDIT -> presenter.editBook(bookDTO)
                    BookActionType.OPEN -> presenter.openBook(bookDTO)
                    else -> throw IllegalArgumentException("Unsupported book action: " + action.name)
                }
            }, true)
        }
    }

    override fun bookSaveSuccess() {
        Snackbar.make(addBookActionMenu, "Book is saved successfully",
                Snackbar.LENGTH_LONG).show()
    }

    override fun bookSaveError(bookDTO: BookDTO) {
        Snackbar.make(addBookActionMenu, "Failure when saving book",
                Snackbar.LENGTH_LONG).setAction("Try again") { presenter::saveBook }.show()
    }

    override fun bookRemoveSuccess() {
        Snackbar.make(addBookActionMenu, "Book is deleted successfully",
                Snackbar.LENGTH_LONG).show()
    }

    override fun bookRemoveError(bookDTO: BookDTO) {
        Snackbar.make(addBookActionMenu, "Failure while deleting book",
                Snackbar.LENGTH_LONG).setAction("Try again") { presenter::deleteBook }.show()
    }

    companion object {

        val newInstance: BookListFragment
            get() = BookListFragment()
    }
}
