package com.pashkobohdan.ttsreader.data.executors.book

import com.pashkobohdan.ttsreader.data.model.database.room.AppDatabase
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.data.usecase.ExchangeUseCase
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor() : ExchangeUseCase<Long, BookDTO>() {

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun getData(request: Long): BookDTO {
        val bookList = appDatabase.bookDAO.bookByIdList(request)
        return if(!bookList.isEmpty()) bookList.first() else throw IllegalArgumentException("Book with id ${request} isn't exist")
    }
}