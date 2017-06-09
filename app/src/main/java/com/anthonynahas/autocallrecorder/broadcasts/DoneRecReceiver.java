package com.anthonynahas.autocallrecorder.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.anthonynahas.autocallrecorder.classes.Res;
import com.anthonynahas.autocallrecorder.services.FetchIntentService;

/**
 * Created by A on 01.05.16.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 01.05.16
 */
public class DoneRecReceiver extends BroadcastReceiver {

    private static final String TAG = DoneRecReceiver.class.getSimpleName();

    public static final String ACTION = "autocallrecorder.intent.action.RECORD_DONE";

    @Override
    public void onReceive(Context context, Intent intent) {

        context.startService(new Intent(context, FetchIntentService.class).putExtra(Res.REC_PARC_KEY,
                intent.getParcelableExtra(Res.REC_PARC_KEY)));
    }
}
