package com.pashkobohdan.ttsreader.executors.book

import com.pashkobohdan.ttsreader.data.usecase.ExchangeUseCase
import com.pashkobohdan.ttsreader.model.database.room.AppDatabase
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor() : ExchangeUseCase<Int, BookDTO>() {

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun getData(request: Int): BookDTO {
        val bookList = appDatabase.bookDAO.bookByIdList(request)
        return if(!bookList.isEmpty()) bookList.first() else throw IllegalArgumentException("Book with id ${request} isn't exist")
    }
}