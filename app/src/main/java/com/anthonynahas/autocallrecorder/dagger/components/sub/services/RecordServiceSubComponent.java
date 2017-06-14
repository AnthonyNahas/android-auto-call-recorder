package com.anthonynahas.autocallrecorder.dagger.components.sub.services;

import com.anthonynahas.autocallrecorder.activities.MainActivity;
import com.anthonynahas.autocallrecorder.services.RecordService;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by anahas on 14.06.2017.
 */

@Subcomponent
public interface RecordServiceSubComponent extends AndroidInjector<RecordService> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<RecordService> {
    }
}
