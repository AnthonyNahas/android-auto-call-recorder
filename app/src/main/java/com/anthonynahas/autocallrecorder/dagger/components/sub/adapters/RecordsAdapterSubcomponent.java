package com.anthonynahas.autocallrecorder.dagger.components.sub.adapters;

import com.anthonynahas.autocallrecorder.adapters.RecordsAdapter;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by A on 14.06.17.
 *
 * @author Anthony Nahas
 *
 */
@Subcomponent
public interface RecordsAdapterSubcomponent extends AndroidInjector<RecordsAdapter> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<RecordsAdapter> {
    }
}
