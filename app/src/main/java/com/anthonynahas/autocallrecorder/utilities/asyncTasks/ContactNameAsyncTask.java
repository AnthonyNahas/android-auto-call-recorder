package com.anthonynahas.autocallrecorder.utilities.asyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.utilities.helpers.ContactHelper;

/**
 * Created by A on 07.06.17.
 */

public class ContactNameAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = ContactNameAsyncTask.class.getSimpleName();

    private static int sCounter = 1;

    private Context mContext;
    private Record mRecord;
    private TextView mTVCallNameOrNumber;

    public ContactNameAsyncTask(Context context, Record record, TextView TVCallNameOrNumber) {
        mContext = context;
        mRecord = record;
        mTVCallNameOrNumber = TVCallNameOrNumber;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String contactName = ContactHelper.getContactName(mContext.getContentResolver(), mRecord.getNumber());
        
        return null;
    }

    @Override
    protected void onPostExecute(String contactName) {

    }
}
