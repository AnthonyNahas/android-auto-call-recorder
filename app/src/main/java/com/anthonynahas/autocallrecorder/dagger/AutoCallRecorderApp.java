package com.anthonynahas.autocallrecorder.dagger;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.support.v4.app.Fragment;

import com.anthonynahas.autocallrecorder.dagger.components.DaggerAppComponent;
import com.anthonynahas.autocallrecorder.dagger.modules.AppModule;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by A on 13.06.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 13.06.17
 */
public class AutoCallRecorderApp extends Application implements
        HasActivityInjector,
        HasServiceInjector,
        HasSupportFragmentInjector,
        HasBroadcastReceiverInjector {

    @Inject
    DispatchingAndroidInjector<Activity> mActivityInjector;

    @Inject
    DispatchingAndroidInjector<Service> mServiceInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> mSupportFragmentInjector;

    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> mBroadcastReceiverInjector;


    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder().appModule(new AppModule(this)).build().inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return mActivityInjector;
    }

    @Override
    public DispatchingAndroidInjector<Service> serviceInjector() {
        return mServiceInjector;
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return mSupportFragmentInjector;
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return mBroadcastReceiverInjector;
    }
}
