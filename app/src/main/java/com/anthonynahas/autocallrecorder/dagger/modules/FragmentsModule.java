package com.anthonynahas.autocallrecorder.dagger.modules;

import com.anthonynahas.autocallrecorder.configurations.Config;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.fragments.RecordsFragementsLoveKey;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.fragments.RecordsFragmentsMainKey;
import com.anthonynahas.autocallrecorder.fragments.RecordsFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by anahas on 19.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 19.06.17
 */
@Module(includes = AppModule.class)
public class FragmentsModule {

    @Provides
    @Singleton
    @RecordsFragmentsMainKey
    RecordsFragment provideRecordsFragmentMain(Config config) {
        return RecordsFragment.newInstance(config.record_fragment_main);
    }

    @Provides
    @Singleton
    @RecordsFragementsLoveKey
    RecordsFragment provideRecordsFragementsLove(Config config) {
        return RecordsFragment.newInstance(config.record_fragment_love);
    }
}
