package com.pashkobohdan.ttsreader.pro.di.modules

import com.pashkobohdan.ttsreader.di.modules.SettingsModule

class ProSettingsModule : SettingsModule(){
    override fun provideIsProVersion() = true
}
