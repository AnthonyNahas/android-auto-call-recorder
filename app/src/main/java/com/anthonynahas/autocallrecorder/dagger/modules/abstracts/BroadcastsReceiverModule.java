package com.anthonynahas.autocallrecorder.dagger.modules.abstracts;

import android.content.BroadcastReceiver;

import com.anthonynahas.autocallrecorder.broadcasts.CallReceiver;
import com.anthonynahas.autocallrecorder.broadcasts.DoneRecReceiver;
import com.anthonynahas.autocallrecorder.dagger.components.sub.broadcasts.CallReceiverSubcomponent;
import com.anthonynahas.autocallrecorder.dagger.components.sub.broadcasts.DoneRecReceiverSubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.BroadcastReceiverKey;
import dagger.multibindings.IntoMap;

/**
 * Created by anahas on 15.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 15.06.17
 */
@Module(subcomponents =
        {
                CallReceiverSubcomponent.class,
                DoneRecReceiverSubcomponent.class
        })
public abstract class BroadcastsReceiverModule {

    @Binds
    @IntoMap
    @BroadcastReceiverKey(CallReceiver.class)
    abstract AndroidInjector.Factory<? extends BroadcastReceiver> bindCallReceiverInjectorFactory(CallReceiverSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @BroadcastReceiverKey(DoneRecReceiver.class)
    abstract AndroidInjector.Factory<? extends BroadcastReceiver> bindDoneRecReceiverInjectorFactory(DoneRecReceiverSubcomponent.Builder builder);
}
