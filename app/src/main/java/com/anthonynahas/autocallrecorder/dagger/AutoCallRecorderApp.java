package com.anthonynahas.autocallrecorder.dagger;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.v4.app.Fragment;

import com.anthonynahas.autocallrecorder.adapters.RecordsAdapter;
import com.anthonynahas.autocallrecorder.dagger.components.DaggerAppComponent;
import com.anthonynahas.autocallrecorder.dagger.modules.AppModule;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.DispatchingAndroidInjector_Factory;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by A on 13.06.17.
 *
 * @author Anthony Nahas#
 */
public class AutoCallRecorderApp extends Application implements
        HasActivityInjector,
        HasSupportFragmentInjector,
        HasServiceInjector {

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
