package com.anthonynahas.autocallrecorder.dagger.components.sub.activities.abstracts;

import com.anthonynahas.autocallrecorder.activities.AppActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by A on 23.06.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 23.06.17
 */

@Subcomponent()
public interface AppActivitySubcomponent extends AndroidInjector<AppActivity> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<AppActivity> {
    }
}
