package com.pashkobohdan.ttsreader.ui.fragments.cloudBook

import android.app.AlertDialog
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.pashkobohdan.ttsreader.R
import com.pashkobohdan.ttsreader.TTSReaderApplication
import com.pashkobohdan.ttsreader.data.cloud.model.CloudBookInfo
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.cloudBookList.CloudBookListPresenter
import com.pashkobohdan.ttsreader.mvp.cloudBookList.view.CloudBookListView
import com.pashkobohdan.ttsreader.ui.adapter.AbstractListItemHolder
import com.pashkobohdan.ttsreader.ui.fragments.cloudBook.widget.CloudBookListItemWidget
import com.pashkobohdan.ttsreader.ui.fragments.common.AbstractListFragment
import java.util.*
import javax.inject.Inject
import javax.inject.Provider


open class CloudBookListFragment : AbstractListFragment<CloudBookListPresenter, CloudBookInfo>(), CloudBookListView {

    @Inject
    lateinit var cloudBookListItemWidgetProvider: Provider<CloudBookListItemWidget>

    @BindView(R.id.book_list_recycler_view)
    lateinit var bookListRecyclerView: RecyclerView
    private lateinit var adapter: ListAdapter

    @InjectPresenter
    lateinit var presenter: CloudBookListPresenter

    val samplePresenter: CloudBookListPresenter
        @ProvidePresenter
        get() = presenterProvider.get()


    override fun onCreate(savedInstanceState: Bundle?) {
        TTSReaderApplication.INSTANCE.getApplicationComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cloud_book_list, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ListAdapter(ArrayList<CloudBookInfo>())

        val mLayoutManager = LinearLayoutManager(context)
        bookListRecyclerView.layoutManager = mLayoutManager
        bookListRecyclerView.adapter = adapter

        setRightHeaderView(createImageHeaderButton(R.drawable.refresh, presenter::refresh))
        addLeftHeaderView(createBackHeaderButton())
        setHeaderTitle("Book Cloud")
    }

    override fun getItemHolder(parent: ViewGroup): AbstractListItemHolder<CloudBookInfo> {
        return cloudBookListItemWidgetProvider.get().getHolder(parent,
                { },
                { book -> presenter.showDetail(book) },
                { book -> presenter.downloadBook(book) })
    }

    override fun showBookListLoadError() {
        Snackbar.make(view!!, "Failure while downloading books",
                Snackbar.LENGTH_LONG).show()
    }

    override fun showBookDownloadError() {
        Snackbar.make(view!!, "Failure while downloading book text",
                Snackbar.LENGTH_LONG).show()
    }

    override fun showBookDownloadSuccessful() {
        Snackbar.make(view!!, "Book is saved successfully",
                Snackbar.LENGTH_LONG).show()
    }

    override fun bookSaveError(bookDTO: BookDTO) {
        Snackbar.make(view!!, "Failure while saving book",
                Snackbar.LENGTH_LONG).show()
    }

    override fun showBookText(title: String, text: String) {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(text)
                .setCancelable(true)
                .create()
                .show()
    }

    override fun showBookList(bookInfoList: List<CloudBookInfo>) {
        adapter.setDataList(bookInfoList)
    }

    override fun showProgress() {
        showProgressWithLock()
    }

    override fun hideProgress() {
        hideProgressWithUnlock()
    }
}
