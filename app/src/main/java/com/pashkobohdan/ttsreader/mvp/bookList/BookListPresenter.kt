package com.pashkobohdan.ttsreader.mvp.bookList

import com.arellomobile.mvp.InjectViewState
import com.pashkobohdan.ttsreader.model.dataExecutors.BookListDataExecutor
import com.pashkobohdan.ttsreader.model.dataExecutors.common.DefaultSubscriber
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookList.view.BookListView
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import com.pashkobohdan.ttsreader.ui.Screen
import javax.inject.Inject

@InjectViewState
class BookListPresenter @Inject constructor() : AbstractPresenter<BookListView>() {

    @Inject
    lateinit var bookListDataExecutor: BookListDataExecutor

    private var bookDTOList: MutableList<BookDTO> = mutableListOf()

    override fun onFirstViewAttach() {
        viewState.showProgress()
        bookListDataExecutor.execute(object : DefaultSubscriber<List<BookDTO>>() {

            override fun onNext(bookDTOs: List<BookDTO>) {
                bookDTOList = bookDTOs.toMutableList()
                viewState.showBookList(bookDTOs)
            }

            override fun onError(t: Throwable) {
                super.onError(t)
                viewState.showDataExecutionError()
            }

            override fun onFinally() {
                viewState.hideProgress()
            }
        })
    }

    fun saveBook(bookDTO: BookDTO) {
        viewState.showProgress()
        bookListDataExecutor.executeAddData(object : DefaultSubscriber<Boolean>() {
            override fun onNext(result: Boolean) {
                if (!bookDTOList.contains(bookDTO)) {
                    bookDTOList.add(bookDTO)
                }
                viewState.bookSaveSuccess()
                viewState.showBookList(bookDTOList)
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                viewState.bookSaveError(bookDTO)
            }

            override fun onFinally() {
                viewState.hideProgress()
            }
        }, bookDTO)
    }

    fun deleteBook(bookDTO: BookDTO) {
        viewState.showProgress()
        bookListDataExecutor.executeDeleteData(object : DefaultSubscriber<Boolean>() {
            override fun onNext(result: Boolean) {
                bookDTOList.remove(bookDTO)
                viewState.showBookList(bookDTOList)
                viewState.bookRemoveSuccess()
            }

            override fun onError(e: Throwable) {
                viewState.bookRemoveError(bookDTO)
            }

            override fun onCompleted() {
                viewState.hideProgress()
            }
        }, bookDTO)
    }

    fun editBook(bookDTO: BookDTO) {
        viewState.showEditBook(bookDTO)
    }

    fun openBook(bookDTO: BookDTO) {
        router.navigateTo(Screen.BOOK_READING, bookDTO)
    }
}
