package com.anthonynahas.autocallrecorder.dagger.modules.activities;

import android.app.Activity;

import com.anthonynahas.autocallrecorder.activities.MainActivity;
import com.anthonynahas.autocallrecorder.dagger.components.sub.activities.MainActivitySubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by A on 13.06.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 14.06.17
 */

@Module(subcomponents = {MainActivitySubcomponent.class,})
public abstract class MainActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindYourActivityInjectorFactory(MainActivitySubcomponent.Builder builder);
}
