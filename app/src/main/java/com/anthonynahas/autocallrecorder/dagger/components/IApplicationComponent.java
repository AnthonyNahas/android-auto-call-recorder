package com.anthonynahas.autocallrecorder.dagger.components;

import android.content.Context;

import com.anthonynahas.autocallrecorder.dagger.MyApplication;
import com.anthonynahas.autocallrecorder.dagger.annotations.ApplicationContext;
import com.anthonynahas.autocallrecorder.dagger.modules.ActivityModule;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Created by A on 13.06.17.
 */

@Component(modules =
        {
                ActivityModule.class,
                AndroidInjectionModule.class,
                MyApplication.class
        })
public interface IApplicationComponent {

    void inject(MyApplication application);

    @ApplicationContext
    Context getContext();
}
