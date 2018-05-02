package com.pashkobohdan.ttsreader.di.modules;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.pashkobohdan.ttsreader.ui.ActivityStartable;
import com.pashkobohdan.ttsreader.ui.PermissionUtil;
import com.pashkobohdan.ttsreader.ui.activities.MainActivity;
import com.pashkobohdan.ttsreader.ui.common.EmptyActivityLifecycleCallbacks;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Context applicationContext;

    public AppModule(Application application) {
        applicationContext = application;
        application.registerActivityLifecycleCallbacks(new EmptyActivityLifecycleCallbacks() {
            @Override
            public void onActivityStarted(Activity activity) {
                applicationContext = activity;
            }
        });
    }

    @Provides
    public Context provideContext() {
        return applicationContext;
    }

    @Provides
    public ActivityStartable provideActivityStartable() {
        if (applicationContext instanceof MainActivity) {
            return (MainActivity) applicationContext;
        }
        return null;
    }

    @Provides
    public PermissionUtil providePermissionUtil() {
        if (applicationContext instanceof MainActivity) {
            return (MainActivity) applicationContext;
        }
        return null;
    }
}
