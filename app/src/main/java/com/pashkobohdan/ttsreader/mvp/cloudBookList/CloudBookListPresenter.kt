package com.pashkobohdan.ttsreader.mvp.cloudBookList

import com.arellomobile.mvp.InjectViewState
import com.pashkobohdan.ttsreader.data.cloud.FirebaseHelper
import com.pashkobohdan.ttsreader.data.cloud.model.CloudBookInfo
import com.pashkobohdan.ttsreader.data.executors.book.SaveBookUseCase
import com.pashkobohdan.ttsreader.data.usecase.observers.UnitObserver
import com.pashkobohdan.ttsreader.mvp.cloudBookList.view.CloudBookListView
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import javax.inject.Inject

@InjectViewState
class CloudBookListPresenter @Inject constructor(): AbstractPresenter<CloudBookListView>() {

    @Inject
    lateinit var saveBookUseCase: SaveBookUseCase

    override fun onFirstViewAttach() {
        refresh()
    }

    fun downloadBook(bookInfo: CloudBookInfo) {
        viewState.showProgress()
        FirebaseHelper.getBookDtoByInfo(bookInfo, { bookDTO ->
            viewState.showProgress()

            saveBookUseCase.execute(bookDTO, object : UnitObserver() {
                override fun onNext() {
                    viewState.showBookDownloadSuccessful()
                }

                override fun onError(e: Throwable) {
                    super.onError(e)
                    viewState.bookSaveError(bookDTO)
                }

                override fun onFinally() {
                    viewState.hideProgress()
                }
            })
        }, {
            viewState.showBookDownloadError()
            viewState.hideProgress()
        })
    }

    fun showDetail(bookInfo: CloudBookInfo) {
        viewState.showBookText(bookInfo.name, bookInfo.text)
    }

    fun refresh() {
        viewState.showProgress()
        FirebaseHelper.readBooksInfo({ bookInfoList ->
            viewState.showBookList(bookInfoList)
            viewState.hideProgress()
        }, {
            viewState.showBookListLoadError()
            viewState.hideProgress()
        })
    }
}