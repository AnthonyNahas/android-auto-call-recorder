package com.anthonynahas.autocallrecorder.views;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.activities.ContactFullscreenActivity;
import com.anthonynahas.autocallrecorder.fragments.RecordsListFragment;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.providers.RecordDbHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.ContactHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.DateTimeHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.ImageHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.MemoryCacheHelper;

import java.util.concurrent.ExecutionException;

/**
 * Created by A on 20.03.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 20.03.17
 */

public class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = RecordViewHolder.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;
    private DateTimeHelper mDateTimeHelper;

    private ImageView call_contact_profile;
    private TextView call_contact_number_or_name;
    private TextView call_date;
    private ImageView call_icon_isIncoming;
    private TextView call_duration;
    private ImageButton mImageCallIsLove;
    public static CheckBox call_selected;


    private long mItemID;


    public RecordViewHolder(View view) {
        super(view);
        mContext = view.getContext();
        mDateTimeHelper = DateTimeHelper.newInstance();
        call_contact_profile = (ImageView) view.findViewById(R.id.iv_profile);
        call_contact_number_or_name = (TextView) view.findViewById(R.id.tv_call_contact_name_or_number);
        call_date = (TextView) view.findViewById(R.id.tv_call_date);
        call_icon_isIncoming = (ImageView) view.findViewById(R.id.iv_call_is_incoming);
        call_duration = (TextView) view.findViewById(R.id.tv_call_duration);

        call_selected = (CheckBox) view.findViewById(R.id.cb_call_selected);
        call_selected.setOnClickListener(this);

        mImageCallIsLove = (ImageButton) view.findViewById(R.id.iv_call_isLove);
        mImageCallIsLove.setOnClickListener(this);

        call_contact_profile.setTag((int) getItemId(), null);
        call_contact_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, ContactFullscreenActivity.class)
                                .putExtra("as", 2),
                        ActivityOptionsCompat
                                .makeSceneTransitionAnimation((Activity) mContext,
                                        call_contact_profile, "photo").toBundle());
            }
        });

    }

    public void setData(Cursor cursor) {
        mCursor = cursor;
        mItemID = Long.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_ID)));
        Log.d(TAG, "cursor position= " + cursor.getPosition());
        final String phoneNumber = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER));

        // Lookup view for data population

        String contact_number_or_name = "";

        try {
            contact_number_or_name = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String contactName = ContactHelper.getContactName(mContext.getContentResolver(), phoneNumber);
                    if (contactName != null) {
                        return contactName;
                    }
                    return phoneNumber;
                }
            }.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Error: ", e);
        }


        if (contact_number_or_name.isEmpty()) {
            call_contact_number_or_name.setText("Unknown");
            //call_contact_number_or_name.setText(phoneNumber);
        } else {
            call_contact_number_or_name.setText(contact_number_or_name);
        }

        long date = cursor.getLong(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE));
        call_date.setText(mDateTimeHelper.getLocalFormatterDate(date));

        int duration = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION));
        call_duration.setText(mDateTimeHelper.getTimeString(duration));

        Bitmap bitmap = MemoryCacheHelper.getBitmapFromMemoryCache(phoneNumber);

        //Bitmap bitmap = null;

        if (bitmap != null) {
            call_contact_profile.setImageBitmap(bitmap);
        } else {
            //BitmapWorkerTask bitmapWorkerTask =  new BitmapWorkerTask(viewHolder.call_contact_profile,context,cursor);
            //bitmapWorkerTask.execute();

            long contactID = cursor.getLong(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_CONTACT_ID));
            Log.d(TAG, "contact id --> " + contactID);
            Bitmap img = ContactHelper.getBitmapForContactID(mContext.getContentResolver(), 1, contactID);

            if (img != null) {
                call_contact_profile.setImageBitmap(img);
//                MemoryCacheHelper.setBitmapToMemoryCache(phoneNumber, img);
            } else {
                //viewHolder.call_contact_profile.setImageResource(Res.drawable.custmtranspprofpic);
                call_contact_profile.setImageBitmap(ImageHelper.decodeSampledBitmapFromResource(mContext.getResources(), R.drawable.custmtranspprofpic60px, 60, 60));
            }
        }

        int isIncomingCall = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_INCOMING));
        //call_icon_isIncoming.setBackgroundColor(isIncomingCall == 1 ? Color.RED : Color.GREEN);
        call_icon_isIncoming.setImageResource(isIncomingCall == 1 ? R.drawable.ic_call_received : R.drawable.ic_call_made);
        call_icon_isIncoming.setColorFilter(isIncomingCall == 1 ? Color.RED : Color.GREEN);

        int isLove = mCursor.getInt(mCursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_IS_LOVE));
        mImageCallIsLove.setImageResource(isLove == 1 ? R.drawable.ic_favorite : R.drawable.ic_favorite_border_black);

        if (RecordsListFragment.sIsInActionMode) {
            call_selected.setVisibility(View.VISIBLE);
            call_selected.setChecked(false);
        } else {
            call_selected.setChecked(false);
            call_selected.setVisibility(View.GONE);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.cb_call_selected:
                RecordsListFragment.getInstance().prepareSelection(view, this.getAdapterPosition());
                break;

            case R.id.iv_call_isLove:
                AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(mContext.getContentResolver()) {
                    @Override
                    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                        if (cursor != null) {
                            cursor.moveToFirst();
                            logCursor(cursor);
                            cursor.moveToFirst();
                            int id = cursor.getInt(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_ID));
                            int isLove = cursor.getInt(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_IS_LOVE));

                            Log.d(TAG, "mImageCallIsLove = " + isLove);
                            int isLoveNew = isLove == 1 ? 0 : 1;
                            if (isLoveNew == 1) {
                                mImageCallIsLove.setImageResource(R.drawable.ic_favorite);
                            } else {
                                mImageCallIsLove.setImageResource(R.drawable.ic_favorite_border_black);
                            }

                            RecordDbHelper
                                    .newInstance()
                                    .updateBooleanColumn(mContext,
                                            RecordDbContract.RecordItem.COLUMN_IS_LOVE, id, isLoveNew);
                        }
                    }
                };

                Log.d(TAG, "mItemId = " + mItemID);
                Uri uri = ContentUris.withAppendedId(RecordDbContract.CONTENT_URL, mItemID);
                asyncQueryHandler.startQuery(0, null, uri, new String[]{"*"}, null, null, null);
                break;

        }
    }

    private void logCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                String contact_id = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID));
                int isLove = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_LOVE));
                Log.d(TAG, "contact id = " + contact_id + " --> isLove = " + isLove);
                //String date = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE));
                //Log.d(TAG, "date = " + date);
            } while (cursor.moveToNext());
        }
        //cursor.close();
    }
}
