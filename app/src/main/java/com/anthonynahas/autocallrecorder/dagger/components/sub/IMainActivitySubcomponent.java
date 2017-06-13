package com.anthonynahas.autocallrecorder.dagger.components.sub;

import com.anthonynahas.autocallrecorder.activities.MainActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by A on 13.06.17.
 */

@Subcomponent
public interface IMainActivitySubcomponent extends AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainActivity> {
    }

}
