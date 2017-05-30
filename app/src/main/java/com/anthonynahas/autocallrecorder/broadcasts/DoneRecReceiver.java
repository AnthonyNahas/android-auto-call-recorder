package com.anthonynahas.autocallrecorder.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.classes.Resources;
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

        context.startService(new Intent(context, FetchIntentService.class).putExtra(Resources.REC_PARC_KEY,
                intent.getParcelableExtra(Resources.REC_PARC_KEY)));
    }
}
