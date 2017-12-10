package com.pashkobohdan.ttsreader.model.database.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.pashkobohdan.ttsreader.model.dto.book.BookDTO;
import com.pashkobohdan.ttsreader.model.dto.book.BookTitleImageDTO;

import java.util.List;

/**
 * Created by bohdan on 10.12.17.
 */

@Dao
public interface BookDAO{

    @Query("SELECT * FROM Book")
    List<BookDTO> getAllBookDtoList();

    @Query("SELECT * FROM BookTitleImage")
    List<BookTitleImageDTO> getAllBookTitleImageDtoList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllBookDTO(BookDTO... bookDTOS);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllBookTitleImageDTO(BookTitleImageDTO... bookTitleImageDTOS);
}
