package com.anthonynahas.autocallrecorder.dagger.components.sub.activities;

import com.anthonynahas.autocallrecorder.activities.RecordsActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by anahas on 14.06.2017.
 */
@Subcomponent()
public interface RecordsActivitySubcomponent extends AndroidInjector<RecordsActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<RecordsActivity> {
    }

}