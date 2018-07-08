package com.pashkobohdan.ttsreader.free.di.modules

import com.pashkobohdan.ttsreader.di.modules.UiModule
import com.pashkobohdan.ttsreader.free.ui.FreeFragmentProvider

class FreeUiModule: UiModule() {

    override fun provideFragmentProvider() = FreeFragmentProvider()
}