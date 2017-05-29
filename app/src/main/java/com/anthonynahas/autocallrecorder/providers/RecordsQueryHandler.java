package com.anthonynahas.autocallrecorder.providers;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.anthonynahas.autocallrecorder.adapters.RecordsCursorRecyclerViewAdapter;

/**
 * Created by anahas on 26.04.2017.
 */

public class RecordsQueryHandler extends AsyncQueryHandler {

    private static RecordsQueryHandler mRecordsQueryHandler;
    private static final String TAG = RecordsQueryHandler.class.getSimpleName();

    //INSERT
    public static final int INSERT = 0;
    public static final int INSERT_DEMO = 1;

    //UPDATE
    public static final int UPDATE_IS_LOVE = 0;

    private RecordsCursorRecyclerViewAdapter mAdapter;

    private RecordsQueryHandler(ContentResolver contentResolver) {
        super(contentResolver);
    }

    public synchronized static RecordsQueryHandler getInstance(ContentResolver contentResolver) {
        if (mRecordsQueryHandler == null) {
            mRecordsQueryHandler = new RecordsQueryHandler(contentResolver);
        }
        return mRecordsQueryHandler;
    }

    public RecordsCursorRecyclerViewAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(RecordsCursorRecyclerViewAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        Log.d(TAG, "onInsertComplete: " + token + " " + cookie + " " + uri);
        if (mAdapter != null) {
            mAdapter.notifyItemInserted(1);
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        Log.d(TAG, "onUpdateComplete: " + token + " " + cookie + " " + result);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        Log.d(TAG, "onDeleteComplete: " + token + " " + cookie + " " + result);
    }

}
