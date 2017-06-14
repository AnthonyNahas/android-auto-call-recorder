package com.anthonynahas.autocallrecorder.dagger.components.sub.services;

import com.anthonynahas.autocallrecorder.services.FetchIntentService;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by anahas on 14.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 14.06.17
 */

@Subcomponent
public interface FetchIntentServiceSubcomponent extends AndroidInjector<FetchIntentService> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<FetchIntentService> {
    }
}
