package com.anthonynahas.autocallrecorder.dagger.modules;

import android.content.Context;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.configurations.Constant;
import com.anthonynahas.autocallrecorder.dagger.annotations.ApplicationContext;
import com.anthonynahas.autocallrecorder.dagger.annotations.RecordsFragment;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.activities.MainActivityKey;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.activities.RecordsActivityKey;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.fragments.RecordsFragmentKey;
import com.anthonynahas.autocallrecorder.utilities.support.ActionModeSupport;

import butterknife.BindString;
import dagger.Module;
import dagger.Provides;

/**
 * Created by anahas on 15.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 15.06.17
 */
@Module(includes = AppModule.class)
public class SupportModule {

    @BindString(R.string.title_activity_main_tabs)
    String title_activity_main_tabs;

    @BindString(R.string.title_activity_rubbished_records)
    String title_activity_rubbished_records;

    @Provides
    @MainActivityKey
    ActionModeSupport provideActionModeSupportForMainActivity(@ApplicationContext Context context, Constant constant) {
        return new ActionModeSupport(context, constant, title_activity_main_tabs, false);
    }

    @Provides
    @RecordsActivityKey
    ActionModeSupport provideActionModeSupportForRecordActivity(@ApplicationContext Context context, Constant constant) {
        return new ActionModeSupport(context, constant, title_activity_rubbished_records, false);
    }

    @Provides
    @RecordsFragmentKey
    ActionModeSupport provideActionModeSupportForRecordsFragment(@ApplicationContext Context context, Constant constant) {
        return new ActionModeSupport(context, constant, "", true);
    }
}
