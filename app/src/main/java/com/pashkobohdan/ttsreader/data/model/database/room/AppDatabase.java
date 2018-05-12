package com.pashkobohdan.ttsreader.data.model.database.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.pashkobohdan.ttsreader.data.model.database.room.dao.BookDAO;
import com.pashkobohdan.ttsreader.data.model.dto.book.BookDTO;
import com.pashkobohdan.ttsreader.data.model.dto.book.BookTitleImageDTO;

/**
 * Created by bohdan on 10.12.17.
 */

@Database(entities = {BookDTO.class, BookTitleImageDTO.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract BookDAO getBookDAO();
}
