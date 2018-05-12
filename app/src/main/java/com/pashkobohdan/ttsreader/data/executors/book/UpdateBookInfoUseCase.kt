package com.pashkobohdan.ttsreader.data.executors.book

import com.pashkobohdan.ttsreader.data.model.database.room.AppDatabase
import com.pashkobohdan.ttsreader.data.usecase.RunUseCase
import javax.inject.Inject

class UpdateBookInfoUseCase @Inject constructor() : RunUseCase<UpdateBookInfoUseCase.BookInfo>() {

    @Inject
    lateinit var appDatabase: AppDatabase

    data class BookInfo(val bookId: Long, val speechRate: Int, val pitchRate: Int, val readingPosition:Int)

    override fun justDoThis(request: BookInfo) {
        appDatabase.bookDAO.updateBookInfo(request.bookId, request.speechRate, request.pitchRate, request.readingPosition)
    }
}