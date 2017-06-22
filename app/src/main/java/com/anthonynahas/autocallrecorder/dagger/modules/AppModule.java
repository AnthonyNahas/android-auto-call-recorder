package com.anthonynahas.autocallrecorder.dagger.modules;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;


import com.anthonynahas.autocallrecorder.dagger.annotations.ApplicationContext;
import com.anthonynahas.autocallrecorder.dagger.annotations.android.HandlerToWaitForLoading;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * The @Module annotation tells Dagger that the AppModule class will provide dependencies for a part
 * of the mApp. It is normal to have multiple Dagger modules in a project, and it is typical
 * for one of them to provide app-wide dependencies.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 12.06.17
 */

@Module
public class AppModule {

    private Application mApp;

    public AppModule(Application app) {
        this.mApp = app;
    }

    @Provides
    @Singleton
    @ApplicationContext
    Context provideContext() {
        return mApp;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return mApp;
    }

    @Provides
    @Singleton
    Resources provideResources(@ApplicationContext Context context) {
        return context.getResources();
    }

    @Provides
    @Singleton
    ContentResolver provideContentResolver() {
        return mApp.getContentResolver();
    }

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApp);
    }

    @Provides
    MediaPlayer provideMediaPlayer() {
        return new MediaPlayer();
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Provides
    @HandlerToWaitForLoading
    Handler provideHandlerToWaitForLoading() {
        return new Handler();
    }
}
