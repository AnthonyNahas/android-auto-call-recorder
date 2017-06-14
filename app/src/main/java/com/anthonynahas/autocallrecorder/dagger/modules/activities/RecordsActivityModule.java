package com.anthonynahas.autocallrecorder.dagger.modules.activities;

import android.app.Activity;

import com.anthonynahas.autocallrecorder.activities.RecordsActivity;
import com.anthonynahas.autocallrecorder.dagger.components.sub.activities.RecordsActivitySubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

/**
 * Created by anahas on 14.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 14.06.17
 */
@Module(subcomponents = {RecordsActivitySubcomponent.class})
public abstract class RecordsActivityModule {

    @Binds
    @IntoMap
    @ActivityKey(RecordsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivityInjectorFactory(RecordsActivitySubcomponent.Builder builder);

}
