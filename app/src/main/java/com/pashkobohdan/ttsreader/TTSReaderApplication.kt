package com.pashkobohdan.ttsreader

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.pashkobohdan.ttsreader.di.ApplicationComponent
import com.pashkobohdan.ttsreader.di.DaggerApplicationComponent
import com.pashkobohdan.ttsreader.di.modules.AppModule
import com.pashkobohdan.ttsreader.di.modules.DatabaseModule

open class TTSReaderApplication : MultiDexApplication() {
    protected var component: ApplicationComponent? = null

    open fun getApplicationComponent(): ApplicationComponent {
        val currentComponent = component
        if (currentComponent == null) {
            component = DaggerApplicationComponent
                    .builder()
                    .appModule(AppModule(this))
                    .databaseModule(DatabaseModule(this))
                    .build()
            return component!!
        } else {
            return currentComponent
        }
    }

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {

        lateinit var INSTANCE: TTSReaderApplication
    }
}
