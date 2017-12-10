package com.pashkobohdan.ttsreader;

import android.app.Application;

import com.pashkobohdan.ttsreader.di.ApplicationComponent;
import com.pashkobohdan.ttsreader.di.DaggerApplicationComponent;
import com.pashkobohdan.ttsreader.di.modules.AppModule;
import com.pashkobohdan.ttsreader.di.modules.DatabaseModule;

public class TTSReaderProApplication extends Application {

    public static TTSReaderProApplication INSTANCE;
    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        //TODO maybe run first getApplicationComponent() here
    }


    public ApplicationComponent getApplicationComponent() {
        if (component == null) {
            component = DaggerApplicationComponent
                    .builder()
                    .appModule(new AppModule(this))
                    .databaseModule(new DatabaseModule(this))
                    .build();
        }
        return component;
    }
}
