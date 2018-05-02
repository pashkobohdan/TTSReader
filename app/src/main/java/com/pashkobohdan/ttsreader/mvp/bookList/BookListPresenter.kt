package com.pashkobohdan.ttsreader.mvp.bookList

import android.Manifest
import com.arellomobile.mvp.InjectViewState
import com.pashkobohdan.ttsreader.model.dataExecutors.BookListDataExecutor
import com.pashkobohdan.ttsreader.model.dataExecutors.common.DefaultSubscriber
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookList.view.BookListView
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import com.pashkobohdan.ttsreader.ui.PermissionUtil
import com.pashkobohdan.ttsreader.ui.Screen
import com.pashkobohdan.ttsreader.utils.TextSplitter
import com.pashkobohdan.ttsreader.utils.fileSystem.file.InternalStorageFileHelper
import com.pashkobohdan.ttsreader.utils.fileSystem.newFileOpening.core.BookReadingResult
import java.io.File
import java.util.*
import javax.inject.Inject

@InjectViewState
class BookListPresenter @Inject constructor() : AbstractPresenter<BookListView>() {

    private val READ_STORAGE_PERMISSION = 1001

    @Inject
    lateinit var bookListDataExecutor: BookListDataExecutor
    @Inject
    lateinit var permissionUtil: PermissionUtil

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
                viewState.showGetBookListError()
            }

            override fun onFinally() {
                viewState.hideProgress()
            }
        })
    }

    fun openNewBook() {
        permissionUtil.requestPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION, {
            viewState.showFileOpenerChooser()
        })
    }

    fun openNewBookSelected(file : File) {
        val fileName = InternalStorageFileHelper.fileNameWithoutExtension(file)
        if(bookDTOList.filter { fileName.equals(it.name) }.isNotEmpty())  {
            viewState.showBookIsAlreadyExistError()
        } else {
            viewState.showBookOpenDialog(file)
        }
    }

    fun bookReadingEnd(bookReadingResult: BookReadingResult?) {
        if(bookReadingResult == null) {
            //TODO reading error
        } else {
            val newBook = BookDTO(bookReadingResult.bookName, bookReadingResult.bookAuthor,
                    bookReadingResult.bookText,
                    TextSplitter.sentencesCount(bookReadingResult.bookText), 0, 60, Date(), Date())
            saveBook(newBook)
        }
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
