package com.anthonynahas.autocallrecorder.dagger;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.v4.app.Fragment;

import com.anthonynahas.autocallrecorder.dagger.components.DaggerAppComponent;
import com.anthonynahas.autocallrecorder.dagger.modules.AppModule;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasDispatchingActivityInjector;
import dagger.android.HasDispatchingServiceInjector;
import dagger.android.support.HasDispatchingSupportFragmentInjector;

/**
 * Created by A on 13.06.17.
 *
 * @author Anthony Nahas#
 */
public class AutoCallRecorderApp extends Application implements
        HasDispatchingActivityInjector,
        HasDispatchingServiceInjector,
        HasDispatchingSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingAndroidActivityInjector;

    @Inject
    DispatchingAndroidInjector<Service> mDispatchingAndroidServiceInjector;

    @Inject
    DispatchingAndroidInjector<Fragment> mDispatchingAndroidSupportFragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent.builder().appModule(new AppModule(this)).build().inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return mDispatchingAndroidActivityInjector;
    }

    @Override
    public DispatchingAndroidInjector<Service> serviceInjector() {
        return mDispatchingAndroidServiceInjector;
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return mDispatchingAndroidSupportFragmentInjector;
    }
}
