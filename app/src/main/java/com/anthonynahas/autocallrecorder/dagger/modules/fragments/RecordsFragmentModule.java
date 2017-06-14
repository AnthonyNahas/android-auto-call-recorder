package com.anthonynahas.autocallrecorder.dagger.modules.fragments;

import android.support.v4.app.Fragment;

import com.anthonynahas.autocallrecorder.dagger.components.sub.fragments.RecordsFragmentSubcomponent;
import com.anthonynahas.autocallrecorder.fragments.RecordsFragment;

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
@Module(subcomponents = {RecordsFragmentSubcomponent.class,})
public abstract class RecordsFragmentModule {

    @Binds
    @IntoMap
    @FragmentKey(RecordsFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindFragmentInjectorFactory(RecordsFragmentSubcomponent.Builder builder);
}
