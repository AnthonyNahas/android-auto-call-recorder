package anthonynahas.com.autocallrecorder.utilities;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by A on 30.04.16.
 */
public class AudiosObserver extends ContentObserver {
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public AudiosObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }
}
