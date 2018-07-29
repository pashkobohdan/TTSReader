package com.pashkobohdan.ttsreader.mvp.cloudBookList

import com.arellomobile.mvp.InjectViewState
import com.pashkobohdan.ttsreader.data.cloud.FirebaseHelper
import com.pashkobohdan.ttsreader.data.cloud.FirebaseHelper.SHOWABLE_BOOK_TEXT_LENGTH
import com.pashkobohdan.ttsreader.data.cloud.model.CloudBookInfo
import com.pashkobohdan.ttsreader.data.executors.book.GetBookListUseCase
import com.pashkobohdan.ttsreader.data.executors.book.SaveBookUseCase
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.data.usecase.observers.DefaultObserver
import com.pashkobohdan.ttsreader.data.usecase.observers.UnitObserver
import com.pashkobohdan.ttsreader.mvp.cloudBookList.view.CloudBookListView
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import javax.inject.Inject

@InjectViewState
class CloudBookListPresenter @Inject constructor() : AbstractPresenter<CloudBookListView>() {

    @Inject
    lateinit var saveBookUseCase: SaveBookUseCase
    @Inject
    lateinit var getBookListUseCase: GetBookListUseCase

    lateinit var currentBookList: List<BookDTO>
    val downloadedInThisSession = mutableSetOf<CloudBookInfo>()
    lateinit var firebaseBookInfoList: MutableSet<CloudBookInfo>

    override fun onFirstViewAttach() {
        viewState.showProgress()
        getBookListUseCase.execute(object : DefaultObserver<List<BookDTO>>() {

            override fun onNext(bookDTOs: List<BookDTO>) {
                currentBookList = bookDTOs.toMutableList()
            }

            override fun onError(e: Throwable) {
                currentBookList = listOf()
            }

            override fun onFinally() {
                viewState.hideProgress()
                refresh()
            }
        })
    }

    fun downloadBook(bookInfo: CloudBookInfo) {
        viewState.showProgress()
        FirebaseHelper.getBookDtoByInfo(bookInfo, { bookDTO ->
            viewState.showProgress()

            saveBookUseCase.execute(bookDTO, object : UnitObserver() {
                override fun onNext() {
                    viewState.showBookDownloadSuccessful()
                    downloadedInThisSession.add(bookInfo)
                    refreshBookListWithAlreadyUploaded()
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
            this.firebaseBookInfoList = bookInfoList.toMutableSet()
            refreshBookListWithAlreadyUploaded()
        }, {
            viewState.showBookListLoadError()
            viewState.hideProgress()
        })
    }

    private fun refreshBookListWithAlreadyUploaded() {
        val nonAddedYetBooks = getNonAddedYetBooks(firebaseBookInfoList)
        viewState.showBookList(nonAddedYetBooks)
        viewState.hideProgress()
    }

    private fun getNonAddedYetBooks(firebaseBooks: Set<CloudBookInfo>): List<CloudBookInfo> {
        val bookSet = mutableSetOf<CloudBookInfo>()
        for (firebaseBook in firebaseBooks) {
            if (!isFirebaseBookAlreadyLoaded(firebaseBook)
                    && !isFirebaseBookLoadedInThisSession(firebaseBook)) {
                bookSet.add(firebaseBook)
            }
        }
        return bookSet.toList()
    }

    private fun isFirebaseBookAlreadyLoaded(firebaseBook: CloudBookInfo): Boolean {
        for (book in currentBookList) {
            if (isBooksEquals(book, firebaseBook)) {
                return true
            }
        }
        return false
    }

    private fun isFirebaseBookLoadedInThisSession(firebaseBook: CloudBookInfo): Boolean {
        for (book in downloadedInThisSession) {
            if (book.equals(firebaseBook)) {
                return true
            }
        }
        return false
    }

    private fun isBooksEquals(book: BookDTO, firebaseBook: CloudBookInfo): Boolean {
        return book.name?.equals(firebaseBook.name) ?: false
                && book.author?.equals(firebaseBook.author) ?: false
                && book.text?.startsWith(firebaseBook.text.substring(0, SHOWABLE_BOOK_TEXT_LENGTH / 2)) ?: false
    }
}