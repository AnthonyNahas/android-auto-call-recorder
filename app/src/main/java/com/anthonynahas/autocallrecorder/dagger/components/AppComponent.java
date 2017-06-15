package com.anthonynahas.autocallrecorder.dagger.components;

import com.anthonynahas.autocallrecorder.activities.MainActivity;
import com.anthonynahas.autocallrecorder.dagger.AutoCallRecorderApp;
import com.anthonynahas.autocallrecorder.dagger.modules.ActivitiesModule;
import com.anthonynahas.autocallrecorder.dagger.modules.AppModule;
import com.anthonynahas.autocallrecorder.dagger.modules.BroadcastsReceiverModule;
import com.anthonynahas.autocallrecorder.dagger.modules.HelperModule;
import com.anthonynahas.autocallrecorder.dagger.modules.ServicesModule;
import com.anthonynahas.autocallrecorder.dagger.modules.FragmentSupportModule;
import com.anthonynahas.autocallrecorder.dagger.modules.SupportModule;

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
                HelperModule.class,
                SupportModule.class,
                ActivitiesModule.class,
                ServicesModule.class,
                FragmentSupportModule.class,
                BroadcastsReceiverModule.class,
                AndroidInjectionModule.class
        })
public interface AppComponent {

    void inject(AutoCallRecorderApp application);

}
