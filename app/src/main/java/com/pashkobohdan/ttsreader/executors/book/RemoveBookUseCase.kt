package com.pashkobohdan.ttsreader.executors.book

import com.pashkobohdan.ttsreader.data.usecase.RunUseCase
import com.pashkobohdan.ttsreader.model.database.room.AppDatabase
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import javax.inject.Inject

class RemoveBookUseCase @Inject constructor() : RunUseCase<BookDTO>() {

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun justDoThis(request: BookDTO) {
        appDatabase.bookDAO.deleteBook(request)
    }
}