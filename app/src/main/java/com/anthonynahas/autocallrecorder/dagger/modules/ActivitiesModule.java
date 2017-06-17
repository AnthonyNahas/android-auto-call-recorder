package com.anthonynahas.autocallrecorder.dagger.modules;

import android.app.Activity;

import com.anthonynahas.autocallrecorder.activities.MainActivity;
import com.anthonynahas.autocallrecorder.activities.RecordsActivity;
import com.anthonynahas.autocallrecorder.activities.SingleContactRecordActivity;
import com.anthonynahas.autocallrecorder.activities.StatisticActivity;
import com.anthonynahas.autocallrecorder.dagger.components.sub.activities.MainActivitySubcomponent;
import com.anthonynahas.autocallrecorder.dagger.components.sub.activities.RecordsActivitySubcomponent;
import com.anthonynahas.autocallrecorder.dagger.components.sub.activities.SingleContactRecordActivitySubcomponent;
import com.anthonynahas.autocallrecorder.dagger.components.sub.activities.StatisticActivitySubcomponent;

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
@Module(subcomponents =
        {
                MainActivitySubcomponent.class,
                RecordsActivitySubcomponent.class,
                StatisticActivitySubcomponent.class,
                SingleContactRecordActivitySubcomponent.class
        })
public abstract class ActivitiesModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindYourActivityInjectorFactory(MainActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(RecordsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivityInjectorFactory(RecordsActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(StatisticActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindStatisticActivityInjectorFactory(StatisticActivitySubcomponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(SingleContactRecordActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindSingleContactRecordActivityInjectorFactory(SingleContactRecordActivitySubcomponent.Builder builder);
}
