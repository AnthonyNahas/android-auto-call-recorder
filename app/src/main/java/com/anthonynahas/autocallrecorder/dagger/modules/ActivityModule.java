package com.anthonynahas.autocallrecorder.dagger.modules;

import android.app.Activity;

import com.anthonynahas.autocallrecorder.activities.MainActivity;
import com.anthonynahas.autocallrecorder.dagger.components.sub.IMainActivitySubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by A on 13.06.17.
 */

@Module(subcomponents = {IMainActivitySubcomponent.class})
public abstract class ActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindYourActivityInjectorFactory(IMainActivitySubcomponent.Builder builder);
}
