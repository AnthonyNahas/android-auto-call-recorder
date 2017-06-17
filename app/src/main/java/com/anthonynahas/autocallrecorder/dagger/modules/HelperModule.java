package com.anthonynahas.autocallrecorder.dagger.modules;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import java.util.HashMap;
import java.util.Map;

import dagger.Module;
import dagger.Provides;

/**
 * Created by anahas on 15.06.2017
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 15.06.2017
 */
@Module(includes = AppModule.class)
public class HelperModule {

    @Provides
    LruCache<String, Bitmap> provideStringBitmapLruCache() {
        final int maxMemorySize = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMemorySize / 10;

        return new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    @Provides
    Map<String, Long> provideLonghashMap() {
        return new HashMap<>(15);
    }

    @Provides
    Map<String, String> provideStringHashMap() {
        return new HashMap<>(15);
    }

}
