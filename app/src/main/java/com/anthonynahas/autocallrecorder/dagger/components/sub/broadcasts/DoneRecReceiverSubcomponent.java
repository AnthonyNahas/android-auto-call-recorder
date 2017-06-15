package com.anthonynahas.autocallrecorder.dagger.components.sub.broadcasts;

import com.anthonynahas.autocallrecorder.broadcasts.DoneRecReceiver;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by anahas on 15.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 15.06.17
 */
@Subcomponent
public interface DoneRecReceiverSubcomponent extends AndroidInjector<DoneRecReceiver> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<DoneRecReceiver> {
    }
}
