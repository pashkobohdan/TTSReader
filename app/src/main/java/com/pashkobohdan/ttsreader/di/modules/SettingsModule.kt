package com.pashkobohdan.ttsreader.di.modules

import com.pashkobohdan.ttsreader.annotations.IsProVersion
import dagger.Module
import dagger.Provides

@Module
open class SettingsModule {

    @Provides
    @IsProVersion
    open fun provideIsProVersion(): Boolean {
        return false
    }
}
