package com.anthonynahas.autocallrecorder.dagger.annotations;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by A on 13.06.17.
 */

@Qualifier
@Retention(RUNTIME)
public @interface ApplicationContext {
}
