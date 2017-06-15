package com.anthonynahas.autocallrecorder.providers;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.anthonynahas.autocallrecorder.adapters.RecordsCursorRecyclerViewAdapter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by anahas on 26.04.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 26.04.17
 */
@Singleton
public class RecordsQueryHandler extends AsyncQueryHandler {

    private static RecordsQueryHandler mRecordsQueryHandler;
    private static final String TAG = RecordsQueryHandler.class.getSimpleName();

    public static final int INSERT_DEMO = 1;

    //UPDATE
    public enum update{
        UPDATE_IS_LOVE,
        UPDATE_IS_LOCKED
    }

    private RecordsCursorRecyclerViewAdapter mAdapter;

    @Inject
    public RecordsQueryHandler(ContentResolver contentResolver) {
        super(contentResolver);
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
