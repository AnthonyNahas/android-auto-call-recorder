package com.anthonynahas.autocallrecorder.dagger.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.anthonynahas.autocallrecorder.dagger.annotations.ApplicationContext;

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
    @ApplicationContext
    Context provideContext() {
        return mApp;
    }

    @Provides
    Application provideApplication() {
        return mApp;
    }

    @Provides
    Resources provideResources(Context context) {
        return context.getResources();
    }

    // Dagger will only look for methods annotated with @Provides
    @Provides
    @Singleton
    // Application reference must come from AppModule.class
    SharedPreferences providesSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mApp);
    }
}
