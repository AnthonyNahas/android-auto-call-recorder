package com.anthonynahas.autocallrecorder.utilities.simulators;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by anahas on 07.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 07.06.17
 */

public class DelaySimulator {

    private static final String TAG = DelaySimulator.class.getSimpleName();

    public static void wait(final int msec) {
        Handler handlerToWait = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
            }
        };

        handlerToWait.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, TAG + " has been fired after " + msec + " msec");
            }
        }, msec);
    }

}
