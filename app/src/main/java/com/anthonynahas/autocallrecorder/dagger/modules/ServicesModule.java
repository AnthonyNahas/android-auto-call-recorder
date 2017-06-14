package com.anthonynahas.autocallrecorder.dagger.modules;

import android.app.Service;

import com.anthonynahas.autocallrecorder.dagger.components.sub.services.FetchIntentServiceSubcomponent;
import com.anthonynahas.autocallrecorder.dagger.components.sub.services.RecordServiceSubComponent;
import com.anthonynahas.autocallrecorder.services.FetchIntentService;
import com.anthonynahas.autocallrecorder.services.RecordService;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.ServiceKey;
import dagger.multibindings.IntoMap;

/**
 * Created by anahas on 14.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 14.06.17
 */
@Module(subcomponents =
        {
                RecordServiceSubComponent.class,
                FetchIntentServiceSubcomponent.class
        })
public abstract class ServicesModule {

    @Binds
    @IntoMap
    @ServiceKey(RecordService.class)
    abstract AndroidInjector.Factory<? extends Service> bindServiceInjectorFactory(RecordServiceSubComponent.Builder builder);

    @Binds
    @IntoMap
    @ServiceKey(FetchIntentService.class)
    abstract AndroidInjector.Factory<? extends Service> bindIntentServiceInjectorFactory(FetchIntentServiceSubcomponent.Builder builder);

}
