package com.pashkobohdan.ttsreader.executors.book

import com.pashkobohdan.ttsreader.data.usecase.GetUseCase
import com.pashkobohdan.ttsreader.model.database.room.AppDatabase
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import javax.inject.Inject


class GetBookListUseCase @Inject constructor() : GetUseCase<List<BookDTO>>() {

    @Inject
    lateinit var appDatabase: AppDatabase

    override fun getData(): List<BookDTO> {
        return appDatabase.getBookDAO().allBookDtoList;
    }

}