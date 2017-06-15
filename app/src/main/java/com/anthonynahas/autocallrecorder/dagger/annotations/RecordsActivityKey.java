package com.anthonynahas.autocallrecorder.dagger.annotations;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * To distinguish that the target object is for the records activity
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 15.06.17
 */
@Qualifier
@Retention(RUNTIME)
public @interface RecordsActivityKey {
}
