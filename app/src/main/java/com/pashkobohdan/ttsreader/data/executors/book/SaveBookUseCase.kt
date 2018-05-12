package com.pashkobohdan.ttsreader.data.executors.book

import com.pashkobohdan.ttsreader.data.model.database.room.AppDatabase
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.data.usecase.RunUseCase
import javax.inject.Inject

class SaveBookUseCase @Inject constructor() : RunUseCase<BookDTO>() {

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun justDoThis(request: BookDTO) {
        appDatabase.bookDAO.insertAllBookDTO(request)
    }
}