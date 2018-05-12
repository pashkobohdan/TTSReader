package com.pashkobohdan.ttsreader.data.executors.book

import com.pashkobohdan.ttsreader.data.model.database.room.AppDatabase
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.data.usecase.GetUseCase
import javax.inject.Inject


class GetBookListUseCase @Inject constructor() : GetUseCase<List<BookDTO>>() {

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun getData(): List<BookDTO> {
        return appDatabase.getBookDAO().allBookDtoList;
    }

}