package com.anthonynahas.autocallrecorder.dagger.components.sub.fragments.dialogs;

import com.anthonynahas.autocallrecorder.fragments.dialogs.RecordsDialog;

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
public interface RecordsDialogSubcomponent extends AndroidInjector<RecordsDialog> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<RecordsDialog> {
    }
}
