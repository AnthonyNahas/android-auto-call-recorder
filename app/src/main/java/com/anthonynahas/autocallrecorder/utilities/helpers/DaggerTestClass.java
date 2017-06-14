package com.anthonynahas.autocallrecorder.utilities.helpers;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by anahas on 14.06.2017.
 */
@Singleton
public class DaggerTestClass {

    @Inject
    public DaggerTestClass() {
    }

    public void print() {
        Log.d("Dagger", "hello world");
    }
}
