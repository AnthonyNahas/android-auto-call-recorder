package com.anthonynahas.autocallrecorder.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.activities.MainOldActivity;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.utilities.helpers.ContactHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.ImageHelper;

/**
 * Created by A on 30.04.16.
 *
 * * @DEPRECATED
 * * @DEPRECATED
 * * @DEPRECATED
 * * @DEPRECATED
 * * @DEPRECATED
 * * @DEPRECATED
 * * @DEPRECATED
 * * @DEPRECATED
 *
 *
 * @author Anthony Nahas
 * @since 30.04.2016
 */
public class RecordsCursorAdapter extends CursorAdapter {

    private static final String TAG = RecordsCursorAdapter.class.getSimpleName();

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public RecordsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        Log.d(TAG, "constructor");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Log.d(TAG, "new View with pos = " + cursor.getPosition());
        View view = LayoutInflater.from(context).inflate(R.layout.item_record, parent, false);
        if (view != null) {
            MyViewHolder viewHolder = new MyViewHolder();
            //viewHolder.position = cursor.getPosition();
            //viewHolder.id = cursor.getString(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_ID));
            //Log.d(TAG,"holderpos = " + viewHolder.position + "  curspos = " + cursor.getPosition());
            viewHolder.call_contact_profile = (ImageView) view.findViewById(R.id.iv_profile);
            viewHolder.call_contact_number_or_name = (TextView) view.findViewById(R.id.tv_call_contact_name_or_number);
            viewHolder.call_date = (TextView) view.findViewById(R.id.tv_call_date);
            viewHolder.call_icon_isIncoming = (ImageView) view.findViewById(R.id.iv_call_is_incoming);
            viewHolder.call_durcation = (TextView) view.findViewById(R.id.tv_call_duration);
            view.setTag(viewHolder);
        }
        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        Log.d(TAG, "bind View");
        // Get the data item for this position
        Log.d(TAG, "cursor position= " + cursor.getPosition());
        final String phoneNumber = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER));
        //if(view != null) {
        // Lookup view for data population
        MyViewHolder viewHolder = (MyViewHolder) view.getTag();
        // Populate the data into the template view using the data object
        //viewHolder.call_contact_profile.setId(cursor.getPosition());

        String contact_number_or_name = "";
        try {
            contact_number_or_name = new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String contactName = ContactHelper.getContactName(context.getContentResolver(), phoneNumber);
                    if (contactName != null) {
                        return contactName;
                    }
                    return phoneNumber;
                }
            }.execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (contact_number_or_name.isEmpty()) {
            viewHolder.call_contact_number_or_name.setText("Unkown");
        } else {
            viewHolder.call_contact_number_or_name.setText(contact_number_or_name);
        }


        switch (cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_INCOMING))) {
            case 0:
                viewHolder.call_icon_isIncoming.setBackgroundColor(Color.GREEN);
                break;
            case 1:
                viewHolder.call_icon_isIncoming.setBackgroundColor(Color.RED);
                break;
        }

        viewHolder.call_date.setText(getLocalFormattedDate(cursor.getLong(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE))));
        viewHolder.call_durcation.setText(String.valueOf(getTimeString(cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION)))));
        Bitmap bitmap = MainOldActivity.getBitmapFromMemoryCache(phoneNumber);
        if (bitmap != null) {
            viewHolder.call_contact_profile.setImageBitmap(bitmap);
        } else {
            //BitmapWorkerTask bitmapWorkerTask =  new BitmapWorkerTask(viewHolder.call_contact_profile,context,cursor);
            //bitmapWorkerTask.execute();
            Bitmap img = ContactHelper.getBitmapForContactID(mContext.getContentResolver(), 1,
                    cursor.getLong(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_CONTACT_ID)));
            if (img != null) {
                viewHolder.call_contact_profile.setImageBitmap(img);
                MainOldActivity.setBitmapToMemoryCache(phoneNumber, img);
            } else {
                //viewHolder.call_contact_profile.setImageResource(R.drawable.custmtranspprofpic);
                viewHolder.call_contact_profile.setImageBitmap(ImageHelper.decodeSampledBitmapFromResource(context.getResources(), R.drawable.custmtranspprofpic60px, 60, 60));
            }
        }
        //new bindImageTask(context,cursor,viewHolder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);

        //// TODO: 14.06.16 found bug - profile pic for those who dnt hv a photoUri
            /*Uri contactPhotoUri = null;
            try {
                contactPhotoUri = new AsyncTask<Void, Void, Uri>() {
                    @Override
                    protected Uri doInBackground(Void... params) {
                        long contactID = cursor.getLong(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_CONTACT_ID));
                        Log.d(TAG,"contactID = " + contactID);
                        if(contactID != 0) {
                            return ContactHelper.getContactPhotoUri(context.getContentResolver(), contactID);
                        }
                        else {
                            return null;
                        }
                    }
                    @Override
                    protected void onPostExecute(Uri uri) {
                        super.onPostExecute(uri);
                        //call_contact_profile.setImageURI(uri);
                    }
                }.execute().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            if (contactPhotoUri != null) {
                call_contact_profile.setImageURI(contactPhotoUri);
                Log.d(TAG,"contactPhotoUri = " + contactPhotoUri);
            }
            else {
                call_contact_profile.setImageResource(R.drawable.custmtranspprofpic);
            }*/
        //}
    }

    private String getLocalFormattedDate(long l) {
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        return dateFormatter.format(new Date(l));
    }

    public static String getTimeString(int duration) {
        int minutes = (int) Math.floor(duration / 1000 / 60);
        int seconds = (duration / 1000) - (minutes * 60);
        return minutes + ":" + String.format("%02d", seconds);
    }

    private static class MyViewHolder {
        int position;
        String id;
        ImageView call_contact_profile;
        TextView call_contact_number_or_name;
        TextView call_date;
        ImageView call_icon_isIncoming;
        TextView call_durcation;
    }

    private static class bindImageTask extends AsyncTask<Void, Void, Uri> {
        private Context mContext;
        private Cursor mCursor;
        private int mPosition;
        private String mID;
        private MyViewHolder mHolder;

        public bindImageTask(Context context, Cursor cursor, MyViewHolder holder) {
            mContext = context;
            mCursor = cursor;
            //mPosition = position;
            mHolder = holder;
        }

        @Override
        protected Uri doInBackground(Void... params) {
            long contactID = mCursor.getLong(mCursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_CONTACT_ID));
            Log.d(TAG, "contactID = " + contactID);
            if (contactID != 0) {
                return ContactHelper.getContactPhotoUri(mContext.getContentResolver(), contactID);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (mHolder.call_contact_profile.getId() == mCursor.getPosition()) {
                mHolder.call_contact_profile.setImageURI(uri);
            } else {
                mHolder.call_contact_profile.setImageResource(R.drawable.custmtranspprofpic);
            }
        }
    }
}
