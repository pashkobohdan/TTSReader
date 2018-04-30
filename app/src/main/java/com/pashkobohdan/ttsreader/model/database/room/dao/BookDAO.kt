package com.pashkobohdan.ttsreader.model.database.room.dao

import android.arch.persistence.room.*
import com.pashkobohdan.ttsreader.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.model.dto.book.BookTitleImageDTO

@Dao
interface BookDAO {

    @get:Query("SELECT * FROM Book")
    val allBookDtoList: List<BookDTO>

    @get:Query("SELECT * FROM BookTitleImage")
    val allBookTitleImageDtoList: List<BookTitleImageDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBookDTO(vararg bookDTOS: BookDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBookTitleImageDTO(vararg bookTitleImageDTOS: BookTitleImageDTO)

    @Delete
    fun deleteBook(vararg bookDTO: BookDTO)
}
