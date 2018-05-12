package com.pashkobohdan.ttsreader.data.model.database.room.dao

import android.arch.persistence.room.*
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO
import com.pashkobohdan.ttsreader.data.model.dto.book.BookTitleImageDTO

@Dao
interface BookDAO {

    @get:Query("SELECT * FROM Book")
    val allBookDtoList: List<BookDTO>

    @Query("SELECT * FROM Book WHERE id LIKE :bookId")
    fun bookByIdList(bookId: Long): List<BookDTO>

    @get:Query("SELECT * FROM BookTitleImage")
    val allBookTitleImageDtoList: List<BookTitleImageDTO>

    @Query("UPDATE Book SET readingSpeed=:speed, readingPitch=:pitch, progress=:readingPosition WHERE id = :bookId")
    fun updateBookInfo(bookId: Long, speed: Int, pitch: Int, readingPosition: Int);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBookDTO(vararg bookDTOS: BookDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllBookTitleImageDTO(vararg bookTitleImageDTOS: BookTitleImageDTO)

    @Delete
    fun deleteBook(vararg bookDTO: BookDTO)
}
