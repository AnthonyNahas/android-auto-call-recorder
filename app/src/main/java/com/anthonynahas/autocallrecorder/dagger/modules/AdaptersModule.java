package com.anthonynahas.autocallrecorder.dagger.modules;

import android.support.v7.widget.RecyclerView;

import com.anthonynahas.autocallrecorder.adapters.RecordsAdapter;
import com.anthonynahas.autocallrecorder.dagger.components.sub.adapters.RecordsAdapterSubcomponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;

/**
 * Created by A on 14.06.17.
 */
@Module
public abstract class AdaptersModule {

    @Binds
    @IntoMap
    @ClassKey(RecordsAdapter.class)
    abstract AndroidInjector.Factory<? extends RecyclerView.Adapter> bindYourActivityInjectorFactory (RecordsAdapterSubcomponent.Builder builder);

}
