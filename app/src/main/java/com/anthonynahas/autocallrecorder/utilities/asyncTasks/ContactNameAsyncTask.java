package com.anthonynahas.autocallrecorder.utilities.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.anthonynahas.autocallrecorder.models.Record;
import com.anthonynahas.autocallrecorder.utilities.helpers.ContactHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.MemoryCacheHelper;

/**
 * Created by A on 07.06.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 08.06.17
 */

public class ContactNameAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = ContactNameAsyncTask.class.getSimpleName();

    private static int sCounter = 1;

    private Context mContext;
    private Record mRecord;
    private TextView mTVCallNameOrNumber;

    public ContactNameAsyncTask(@NonNull Context context,
                                @NonNull Record record,
                                @NonNull TextView TVCallNameOrNumber) {
        mContext = context;
        mRecord = record;
        mTVCallNameOrNumber = TVCallNameOrNumber;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String cachedContactName = MemoryCacheHelper.getMemoryCacheForContactsName(mRecord.getNumber());

        String contactName = cachedContactName != null ?
                cachedContactName
                :
                ContactHelper.getContactName(mContext.getContentResolver(), mRecord.getNumber());

        if (cachedContactName == null && contactName != null) {
            mRecord.setName(contactName);
            MemoryCacheHelper.setContactNameToMemoryCache(mRecord.getNumber(), mRecord.getName());
            return contactName;
        }

        return null;
    }

    @Override
    protected void onPostExecute(final String contactName) {
        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
            }
        }.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (contactName != null && !contactName.isEmpty()) {
                    mTVCallNameOrNumber.setText(contactName);
                }
                Log.d(TAG, "done " + sCounter);
            }
        }, 100 * sCounter++);
    }
}
