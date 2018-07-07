package com.pashkobohdan.ttsreader.free

import com.pashkobohdan.ttsreader.TTSReaderApplication
import com.pashkobohdan.ttsreader.di.ApplicationComponent
import com.pashkobohdan.ttsreader.di.DaggerApplicationComponent
import com.pashkobohdan.ttsreader.di.modules.AppModule
import com.pashkobohdan.ttsreader.di.modules.DatabaseModule
import com.pashkobohdan.ttsreader.free.di.modules.FreeSettingsModule

class TTSReaderFreeApplication : TTSReaderApplication() {

    override fun getApplicationComponent(): ApplicationComponent {
        if (component == null) {
            component = DaggerApplicationComponent
                    .builder()
                    .appModule(AppModule(this))
                    .databaseModule(DatabaseModule(this))
                    .settingsModule(FreeSettingsModule())
                    .build()
        }
        return component!!
    }
}
