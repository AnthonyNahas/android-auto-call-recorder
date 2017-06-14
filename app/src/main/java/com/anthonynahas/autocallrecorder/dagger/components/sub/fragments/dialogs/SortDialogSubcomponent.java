package com.anthonynahas.autocallrecorder.dagger.components.sub.fragments.dialogs;

import com.anthonynahas.autocallrecorder.fragments.dialogs.SortDialog;

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
public interface SortDialogSubcomponent extends AndroidInjector<SortDialog> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<SortDialog> {
    }
}
