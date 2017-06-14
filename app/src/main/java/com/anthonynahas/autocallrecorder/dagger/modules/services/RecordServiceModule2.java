package com.anthonynahas.autocallrecorder.dagger.modules.services;

import android.content.Context;

import com.anthonynahas.autocallrecorder.services.RecordService;

import dagger.Component;
import dagger.Module;
import dagger.Provides;

/**
 * Created by anahas on 14.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 14.06.17
 */
@Module
public class RecordServiceModule2 {

    private RecordService mRecordService;

    public RecordServiceModule2(RecordService mRecordService) {
        this.mRecordService = mRecordService;
    }

    @Provides
    RecordService provideRecordService() {
        return mRecordService;
    }

    @Provides
    Context provideContext() {
        return mRecordService;
    }

//    @Component(modules = RecordServiceModule2.class)
//    interface RecordServiceComponent {
//        void inject(RecordService recordService);
//    }
}
