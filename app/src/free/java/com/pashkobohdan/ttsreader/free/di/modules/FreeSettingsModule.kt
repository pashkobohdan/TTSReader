package com.pashkobohdan.ttsreader.free.di.modules

import com.pashkobohdan.ttsreader.di.modules.SettingsModule

class FreeSettingsModule : SettingsModule() {
    override fun provideIsProVersion() = false
}