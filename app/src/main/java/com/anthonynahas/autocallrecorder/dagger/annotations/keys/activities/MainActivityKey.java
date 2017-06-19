package com.anthonynahas.autocallrecorder.dagger.annotations.keys.activities;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by anahas on 19.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 19.06.17
 */

@Qualifier
@Retention(RUNTIME)
public @interface MainActivityKey {
}
