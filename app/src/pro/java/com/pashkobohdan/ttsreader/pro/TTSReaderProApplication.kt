package com.pashkobohdan.ttsreader.pro

import com.pashkobohdan.ttsreader.TTSReaderApplication
import com.pashkobohdan.ttsreader.di.ApplicationComponent
import com.pashkobohdan.ttsreader.di.DaggerApplicationComponent
import com.pashkobohdan.ttsreader.di.modules.AppModule
import com.pashkobohdan.ttsreader.di.modules.DatabaseModule
import com.pashkobohdan.ttsreader.pro.di.modules.ProSettingsModule

class TTSReaderProApplication : TTSReaderApplication() {

    override fun getApplicationComponent(): ApplicationComponent {
        if (component == null) {
            component = DaggerApplicationComponent
                    .builder()
                    .appModule(AppModule(this))
                    .databaseModule(DatabaseModule(this))
                    .settingsModule(ProSettingsModule())
                    .build()
        }
        return component!!
    }
}
