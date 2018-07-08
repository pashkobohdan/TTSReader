package com.pashkobohdan.ttsreader.mvp.bookList

import android.Manifest
import com.arellomobile.mvp.InjectViewState
import com.pashkobohdan.ttsreader.data.cloud.FirebaseHelper
import com.pashkobohdan.ttsreader.data.executors.book.GetBookListUseCase
import com.pashkobohdan.ttsreader.data.executors.book.RemoveBookUseCase
import com.pashkobohdan.ttsreader.data.executors.book.SaveBookUseCase
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.data.usecase.observers.DefaultObserver
import com.pashkobohdan.ttsreader.data.usecase.observers.UnitObserver
import com.pashkobohdan.ttsreader.mvp.bookList.view.BookListView
import com.pashkobohdan.ttsreader.mvp.common.AbstractPresenter
import com.pashkobohdan.ttsreader.ui.PermissionUtil
import com.pashkobohdan.ttsreader.ui.Screen
import com.pashkobohdan.ttsreader.utils.Constants.DEFAULT_PITCH_RATE
import com.pashkobohdan.ttsreader.utils.Constants.DEFAULT_SPEED_RATE
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
    lateinit var getBookListUseCase: GetBookListUseCase
    @Inject
    lateinit var saveBookUseCase: SaveBookUseCase
    @Inject
    lateinit var removeBookUseCase: RemoveBookUseCase
    @Inject
    lateinit var permissionUtil: PermissionUtil

    private var bookDTOList: MutableList<BookDTO> = mutableListOf()

    override fun onFirstViewAttach() {
        refresh()
    }

    fun openNewBook() {
        permissionUtil.requestPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION, {
            viewState.showFileOpenerChooser()
        })
    }

    fun openNewBookSelected(file: File) {
        val fileName = InternalStorageFileHelper.fileNameWithoutExtension(file)
        if (bookDTOList.filter { fileName.equals(it.name) }.isNotEmpty()) {
            viewState.showBookIsAlreadyExistError()
        } else {
            viewState.showBookOpenDialog(file)
        }
    }

    fun bookReadingEnd(bookReadingResult: BookReadingResult?) {
        if (bookReadingResult == null) {
            //TODO reading error
        } else {
            val newBook = BookDTO(bookReadingResult.bookName, bookReadingResult.bookAuthor,
                    bookReadingResult.bookText,
                    TextSplitter.sentencesCount(bookReadingResult.bookText),
                    0, DEFAULT_SPEED_RATE, DEFAULT_PITCH_RATE,
                    Date(), Date())
            saveBook(newBook)
        }
    }

    fun saveBook(bookDTO: BookDTO) {
        viewState.showProgress()
        saveBookUseCase.execute(bookDTO, object : UnitObserver() {

            override fun onNext() {
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
        })
    }

    fun deleteBook(bookDTO: BookDTO) {
        viewState.showProgress()
        removeBookUseCase.execute(bookDTO, object : UnitObserver() {

            override fun onNext() {
                bookDTOList.remove(bookDTO)
                viewState.showBookList(bookDTOList)
                viewState.bookRemoveSuccess()
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                viewState.bookRemoveError(bookDTO)
            }

            override fun onFinally() {
                viewState.hideProgress()
            }
        })
    }

    fun refresh() {
        viewState.showProgress()
        getBookListUseCase.execute(object : DefaultObserver<List<BookDTO>>() {

            override fun onNext(bookDTOs: List<BookDTO>) {
                bookDTOList = bookDTOs.toMutableList()
                viewState.showBookList(bookDTOs)
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                viewState.showGetBookListError()
            }

            override fun onFinally() {
                viewState.hideProgress()
            }
        })
    }

    fun editBook(bookDTO: BookDTO) {
        viewState.showEditBook(bookDTO)
    }

    fun openBook(bookDTO: BookDTO) {
        router.navigateTo(Screen.BOOK_READING, bookDTO.id)
    }

    fun downloadFromCloud() {
        router.navigateTo(Screen.CLOUD_BOOK_LIST)
    }

    fun uploadToStorage(bookDTO: BookDTO) {
        FirebaseHelper.uploadBookDTO(bookDTO, {
            viewState.bookUploadSuccess()
        }, {
            viewState.bookRemoveError(bookDTO)
        })
    }
}
