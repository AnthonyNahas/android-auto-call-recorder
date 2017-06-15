package com.anthonynahas.autocallrecorder.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.anthonynahas.autocallrecorder.configurations.Constant;
import com.anthonynahas.autocallrecorder.services.FetchIntentService;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

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

    @Inject
    Constant mConstant;

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidInjection.inject(this, context);

        context.startService(new Intent(context, FetchIntentService.class).putExtra(mConstant.REC_PARC_KEY,
                intent.getParcelableExtra(mConstant.REC_PARC_KEY)));
    }
}
