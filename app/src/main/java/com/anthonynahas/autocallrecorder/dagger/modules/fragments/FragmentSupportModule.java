package com.anthonynahas.autocallrecorder.dagger.modules.fragments;

import android.support.v4.app.Fragment;

import com.anthonynahas.autocallrecorder.dagger.components.sub.fragments.RecordsFragmentSubcomponent;
import com.anthonynahas.autocallrecorder.dagger.components.sub.fragments.dialogs.RecordsDialogSubcomponent;
import com.anthonynahas.autocallrecorder.dagger.components.sub.fragments.dialogs.SortDialogSubcomponent;
import com.anthonynahas.autocallrecorder.fragments.RecordsFragment;
import com.anthonynahas.autocallrecorder.fragments.dialogs.RecordsDialog;
import com.anthonynahas.autocallrecorder.fragments.dialogs.SortDialog;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Created by anahas on 14.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 14.06.17
 */
@Module(subcomponents =
        {
                RecordsFragmentSubcomponent.class,
                RecordsDialogSubcomponent.class,
                SortDialogSubcomponent.class
        })
public abstract class FragmentSupportModule {

    @Binds
    @IntoMap
    @FragmentKey(RecordsFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindRecordsFragmentInjectorFactory(RecordsFragmentSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(RecordsDialog.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindRecordsDialogInjectorFactory(RecordsDialogSubcomponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(SortDialog.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindSortDialogInjectorFactory(SortDialogSubcomponent.Builder builder);
}
