package com.anthonynahas.autocallrecorder.events.search;

import android.os.Bundle;

/**
 * Created by anahas on 20.06.2017.
 *
 * @author Anthony Nahas
 * @since 20.06.2017
 */

public class OnQueryChangedEvent {

    public Bundle args;

    public OnQueryChangedEvent(Bundle args) {
        this.args = args;
    }
}
