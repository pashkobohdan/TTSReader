package com.pashkobohdan.ttsreader.ui.fragments.book

import android.os.Bundle
import android.os.Environment
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
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookList.BookListPresenter
import com.pashkobohdan.ttsreader.mvp.bookList.view.BookListView
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder
import com.pashkobohdan.ttsreader.ui.dialog.BookEditDialog
import com.pashkobohdan.ttsreader.ui.dialog.DialogUtils
import com.pashkobohdan.ttsreader.ui.fragments.book.widget.BookListItemWidget
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractListFragment
import com.pashkobohdan.ttsreader.ui.fragments.fileChoose.FileChooserDialog
import com.pashkobohdan.ttsreader.ui.tools.UpDownScrollListener
import com.pashkobohdan.ttsreader.utils.Constants
import com.pashkobohdan.ttsreader.utils.enums.BookActionType
import rx.functions.Action0
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

class BookListFragment : AbstractListFragment<BookListPresenter, BookDTO>(), BookListView {

    @InjectPresenter
    lateinit var presenter: BookListPresenter
    @Inject
    lateinit var bookListItemWidgetProvider: Provider<BookListItemWidget>
    @Inject
    lateinit var fileChooserDialogProvider: Provider<FileChooserDialog>

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
        //TODO open select file chooser
        //        FileListerDialog fileListerDialog = FileListerDialog.createFileListerDialog(getContext());
        //        fileListerDialog.setOnFileSelectedListener((file, path) -> {
        //your code here
        fileChooserDialogProvider.get().show(Environment.getExternalStorageDirectory(), { pathname -> true }, "Choose a path", { file1 ->

        }) {

        }

        //        });
        //        fileListerDialog.setDefaultDir(new File("/"));//TODO replace
        //        fileListerDialog.setFileFilter(FileListerDialog.FILE_FILTER.ALL_FILES);
        //        fileListerDialog.show();
    }

    @OnClick(R.id.add_book_create_fab)
    internal fun createBookButtonClicked() {
        addBookActionMenu!!.close(true)
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

    override fun showBookList(bookDTOList: List<BookDTO>) {
        adapter.setDataList(bookDTOList)
    }

    override fun showEditBook(bookDTO: BookDTO) {
        val dialog = BookEditDialog(context!!, bookDTO, presenter::saveBook)
        dialog.show()
    }

    override fun deleteBook(deletedBookPosition: Int) {
        adapter.notifyItemChanged(deletedBookPosition)
        adapter.notifyItemChanged(deletedBookPosition + 1)//TODO think about this
    }

    private fun showCreateNewBookDialog() {
        val dialog = BookEditDialog(context!!, null, presenter::saveBook)
        dialog.show()
    }

    override fun showDataExecutionError() {
        DialogUtils.showAlert("Error", "Data execution error, try later", context!!, Action0 {  })//TODO move to string.xml
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
