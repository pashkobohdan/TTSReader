package com.pashkobohdan.ttsreader.di.modules

import com.pashkobohdan.ttsreader.data.storage.UserStorage
import com.pashkobohdan.ttsreader.data.storage.impl.ApplicationStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Singleton
    @Provides
    fun provideUserStorage(appStorage: ApplicationStorage): UserStorage = appStorage
}