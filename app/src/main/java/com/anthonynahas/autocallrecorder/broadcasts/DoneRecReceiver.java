package com.anthonynahas.autocallrecorder.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.anthonynahas.autocallrecorder.services.FetchIntentService;

/**
 * Created by A on 01.05.16.
 */
public class DoneRecReceiver extends BroadcastReceiver {

    private static final String TAG = DoneRecReceiver.class.getSimpleName();

    private static Intent sIntentFetching;

    public static final String ACTION = "autocallrecorder.intent.action.RECORD_DONE";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG,"onReceive()");
        Log.d(TAG,"action = " + intent.getAction());

        Bundle conData = intent.getExtras();

        /*
        if(sIntentFetching == null){
            sIntentFetching = new Intent(context, FetchIntentService.class);
            if(conData != null){
                sIntentFetching.putExtras(conData);
            }
        }*/

        Intent i =  new Intent(context, FetchIntentService.class);
        if(conData != null){
            i.putExtras(conData);
        }
        context.startService(i);
    }
}
