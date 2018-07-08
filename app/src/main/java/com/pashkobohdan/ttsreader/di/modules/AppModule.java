package com.pashkobohdan.ttsreader.di.modules;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.pashkobohdan.ttsreader.data.usecase.scheduler.ThreadPoolScheduler;
import com.pashkobohdan.ttsreader.data.usecase.scheduler.impl.ThreadPoolSchedulerImpl;
import com.pashkobohdan.ttsreader.ui.ActivityStartable;
import com.pashkobohdan.ttsreader.ui.PermissionUtil;
import com.pashkobohdan.ttsreader.ui.ProgressUtil;
import com.pashkobohdan.ttsreader.ui.activities.MainActivity;
import com.pashkobohdan.ttsreader.ui.common.EmptyActivityLifecycleCallbacks;

import javax.inject.Singleton;

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
                //Fix for ads activity (interstitial)
                if(activity instanceof MainActivity) {
                    applicationContext = activity;
                }
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

    @Provides
    public ProgressUtil provideProgressUtil() {
        if (applicationContext instanceof MainActivity) {
            return (MainActivity) applicationContext;
        }
        return null;
    }

    @Provides
    @Singleton
    public ThreadPoolScheduler provideThreadPoolScheduler(ThreadPoolSchedulerImpl threadPoolScheduler) {
        return threadPoolScheduler;
    }
}
