package com.pashkobohdan.ttsreader.mvp.bookList.view

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.mvp.bookList.BookListPresenter
import com.pashkobohdan.ttsreader.mvp.common.AbstractScreenView
import java.io.File

@StateStrategyType(SkipStrategy::class)
interface BookListView : AbstractScreenView<BookListPresenter> {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showBookList(bookDTOList: List<BookDTO>)

    fun showEditBook(bookDTO: BookDTO)

    fun showGetBookListError()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showProgress()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun hideProgress()

    fun bookSaveSuccess()

    fun bookSaveError(bookDTO: BookDTO)

    fun bookRemoveSuccess()

    fun bookRemoveError(bookDTO: BookDTO)

    fun showFileOpenerChooser()

    fun showBookOpenDialog(file: File)

    fun showBookIsAlreadyExistError()
}
