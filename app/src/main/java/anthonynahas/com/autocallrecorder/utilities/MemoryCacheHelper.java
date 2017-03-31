package anthonynahas.com.autocallrecorder.utilities;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Class that deals with the memory cache to load quickly bitmaps
 * of the profile picture of a contact.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 31.03.2017
 */

public class MemoryCacheHelper {

    /************** Memory Cache ***************/
    private static LruCache<String, Bitmap> mMemoryCache;

    public static void init(){
        final int maxMemorySize = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMemorySize / 10;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    /**************
     * Memory Cache
     ***************/
    public static Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public static void setBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
}
