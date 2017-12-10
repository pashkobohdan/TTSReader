package com.pashkobohdan.ttsreader.di.modules;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.pashkobohdan.ttsreader.model.database.room.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    private AppDatabase database;

    public DatabaseModule(Application application) {
        database = Room.databaseBuilder(application.getApplicationContext(), AppDatabase.class,
                "book_database").build();
    }

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase() {
        return database;
    }
}
