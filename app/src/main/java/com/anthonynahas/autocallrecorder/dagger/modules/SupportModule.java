package com.anthonynahas.autocallrecorder.dagger.modules;

import android.content.Context;

import com.anthonynahas.autocallrecorder.configurations.Constant;
import com.anthonynahas.autocallrecorder.dagger.annotations.ApplicationContext;
import com.anthonynahas.autocallrecorder.dagger.annotations.RecordsActivityKey;
import com.anthonynahas.autocallrecorder.utilities.support.ActionModeSupport;

import dagger.Module;
import dagger.Provides;

/**
 * Created by anahas on 15.06.2017.
 */
@Module(includes = AppModule.class)
public class SupportModule {

    @Provides
    @RecordsActivityKey
    ActionModeSupport provideActionModeSupport(@ApplicationContext Context context, Constant constant){
        return new ActionModeSupport(context, constant,"",false);
    }
}
