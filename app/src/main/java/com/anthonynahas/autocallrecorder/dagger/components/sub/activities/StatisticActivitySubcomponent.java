package com.anthonynahas.autocallrecorder.dagger.components.sub.activities;

import com.anthonynahas.autocallrecorder.activities.StatisticActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by A on 17.06.17.
 */
@Subcomponent()
public interface StatisticActivitySubcomponent extends AndroidInjector<StatisticActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<StatisticActivity> {
    }
}
