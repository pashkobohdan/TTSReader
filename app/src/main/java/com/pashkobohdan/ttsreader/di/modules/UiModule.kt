package com.pashkobohdan.ttsreader.di.modules

import com.pashkobohdan.ttsreader.ui.navigation.FragmentProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class UiModule {

    @Singleton
    @Provides
    open fun provideFragmentProvider() = FragmentProvider()
}