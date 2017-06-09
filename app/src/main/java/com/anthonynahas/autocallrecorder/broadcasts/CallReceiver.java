package com.anthonynahas.autocallrecorder.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.classes.Res;
import com.anthonynahas.autocallrecorder.services.RecordService;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;
import com.anthonynahas.autocallrecorder.services.FetchIntentService;

/**
 * Created by A on 04.04.16. using telephonymanager API
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 04.04.16
 */
public class CallReceiver extends BroadcastReceiver {

    private static final String TAG = CallReceiver.class.getSimpleName();

    public static final String PHONE_STATE = "android.intent.action.PHONE_STATE";
    public static final String OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";

    private static final String IDLE = "IDLE";
    private static final String OFFHOOK = "OFFHOOK";
    private static final String RINGING = "RINGING";

    public static final String LONGDATEKEY = "longdatekey";
    public static final String INCOMINGCALLKEY = "incomingcallkey";
    public static final String NUMBERKEY = "numberkey";

    private static boolean OUTGOING = false;
    private static boolean INCOMING = false;
    private static boolean ANSWERED = false;

    private static Intent sIntent;
    private static Intent sIntentFetching;
    private static String sNumber;

    private Record mRecord;


    @Override
    public void onReceive(Context context, Intent intent) {

        PreferenceHelper preferenceHelper = new PreferenceHelper(context);

        if (preferenceHelper.canAutoRecord()) {

            String action = intent.getAction();
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (action.equals(OUTGOING_CALL)) {
                OUTGOING = true;
                sNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Log.d(TAG, "outgoing number is: " + sNumber);
                Log.d(TAG, "real action = " + action);
                Log.d(TAG, "state = " + state);
            }


            if (action.equals(PHONE_STATE)) {
                if (state != null) {
                    if (state.equals(RINGING)) {
                        INCOMING = true;
                        sNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                        Log.d(TAG, "mobile phone is ringing..." + sNumber);
                    }
                }
            }

            if (OUTGOING || INCOMING && action.equals(PHONE_STATE)) {
                if (state != null) {
                    if (sIntent == null) {
                        sIntent = new Intent(context, RecordService.class);
                    }
                    if (state.equals(OFFHOOK)) {
                        ANSWERED = true;
                        Log.d(TAG, "start recording --> offhook");
                        mRecord = new Record();
                        mRecord.setDate(System.currentTimeMillis());
                        mRecord.setIncoming(INCOMING);
                        // TODO: 30.05.2017 if - else should be replaced
                        if (OUTGOING) {
//                            sConData.putInt(INCOMINGCALLKEY, 0);
                        } else if (INCOMING) {
//                            sConData.putInt(INCOMINGCALLKEY, 1);
                        }
                        mRecord.setNumber(sNumber);
                        sIntent.putExtra(Res.REC_PARC_KEY, (Parcelable) mRecord);
                        context.startService(sIntent);
                    } else if (state.equals(IDLE)) {
                        OUTGOING = false;
                        INCOMING = false;
                        Log.d(TAG, "call has been cancelled");
                        if (ANSWERED) {
                            Log.d(TAG, "stop recording");
                            context.stopService(sIntent);
                            ANSWERED = false;
                            Log.d(TAG, "number = " + sNumber);
                            if (sIntentFetching == null) {
                                sIntentFetching = new Intent(context, FetchIntentService.class);
                                sIntent.putExtra(Res.REC_PARC_KEY, (Parcelable) mRecord);
                            }
                        }
                    }
                }
            }
        }
    }
}
