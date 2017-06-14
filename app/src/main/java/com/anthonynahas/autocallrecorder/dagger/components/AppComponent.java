package com.anthonynahas.autocallrecorder.dagger.components;

import com.anthonynahas.autocallrecorder.dagger.AutoCallRecorderApp;
import com.anthonynahas.autocallrecorder.dagger.modules.activities.MainActivityModule;
import com.anthonynahas.autocallrecorder.dagger.modules.AppModule;
import com.anthonynahas.autocallrecorder.dagger.modules.activities.RecordsActivityModule;
import com.anthonynahas.autocallrecorder.dagger.modules.fragments.RecordsFragmentModule;
import com.anthonynahas.autocallrecorder.dagger.modules.services.FetchIntentServiceModule;
import com.anthonynahas.autocallrecorder.dagger.modules.services.RecordServiceModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by A on 13.06.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 13.06.17
 */
@Singleton
@Component(modules =
        {
                AppModule.class,
                MainActivityModule.class,
                RecordsActivityModule.class,
                RecordServiceModule.class,
                FetchIntentServiceModule.class,
                RecordsFragmentModule.class,
                AndroidInjectionModule.class
        })
public interface AppComponent {

    void inject(AutoCallRecorderApp application);

}
