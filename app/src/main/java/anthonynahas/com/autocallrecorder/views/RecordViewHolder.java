package anthonynahas.com.autocallrecorder.views;

import android.content.AsyncQueryHandler;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.fragments.RecordsRecyclerListFragment;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;
import anthonynahas.com.autocallrecorder.providers.RecordDbHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.ContactHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.ImageHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.MemoryCacheHelper;

/**
 * Created by A on 20.03.17.
 */

public class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final String TAG = RecordViewHolder.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;

    private ImageView call_contact_profile;
    private TextView call_contact_number_or_name;
    private TextView call_date;
    private ImageView call_icon_isIncoming;
    private TextView call_duration;
    private ImageButton call_isLove;
    public static AppCompatCheckBox call_selected;


    private long mItemID;


    public RecordViewHolder(Context context, View view) {
        super(view);
        mContext = context;
        call_contact_profile = (ImageView) view.findViewById(R.id.img_profile);
        call_contact_number_or_name = (TextView) view.findViewById(R.id.call_contact_name_number);
        call_date = (TextView) view.findViewById(R.id.call_date);
        call_icon_isIncoming = (ImageView) view.findViewById(R.id.call_icon_isIncoming);
        call_duration = (TextView) view.findViewById(R.id.call_duration);

        call_selected = (AppCompatCheckBox) view.findViewById(R.id.call_selected);
        call_selected.setOnClickListener(this);

        call_isLove = (ImageButton) view.findViewById(R.id.call_isLove);
        call_isLove.setOnClickListener(this);

    }

    public void setData(Cursor cursor) {
        mCursor = cursor;
        mItemID = Long.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_ID)));
        Log.d(TAG, "cursor position= " + cursor.getPosition());
        final String phoneNumber = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER));
        //if(view != null) {
        // Lookup view for data population

        //RecordsCursorAdapter.MyViewHolder viewHolder = (RecordsCursorAdapter.MyViewHolder) view.getTag();

        // Populate the data into the template view using the data object
        //viewHolder.call_contact_profile.setId(cursor.getPosition());

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
            call_contact_number_or_name.setText("Unkown");
            //call_contact_number_or_name.setText(phoneNumber);
        } else {
            call_contact_number_or_name.setText(contact_number_or_name);
        }

        call_date.setText(getLocalFormatterDate(cursor.getLong(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE))));
        //call_date.setText(cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE)));

        call_duration.setText(String.valueOf(getTimeString(cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION)))));

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
                MemoryCacheHelper.setBitmapToMemoryCache(phoneNumber, img);
            } else {
                //viewHolder.call_contact_profile.setImageResource(R.drawable.custmtranspprofpic);
                call_contact_profile.setImageBitmap(ImageHelper.decodeSampledBitmapFromResource(mContext.getResources(), R.drawable.custmtranspprofpic60px, 60, 60));
            }
        }

        int isIncomingCall = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_INCOMING));
        //call_icon_isIncoming.setBackgroundColor(isIncomingCall == 1 ? Color.RED : Color.GREEN);
        call_icon_isIncoming.setImageResource(isIncomingCall == 1 ? R.drawable.ic_call_received : R.drawable.ic_call_made);
        call_icon_isIncoming.setColorFilter(isIncomingCall == 1 ? Color.RED : Color.GREEN);

        int isLove = mCursor.getInt(mCursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_IS_LOVE));
        call_isLove.setImageResource(isLove == 1 ? R.drawable.ic_favorite : R.drawable.ic_favorite_border_black);

        if (RecordsRecyclerListFragment.sIsInActionMode) {
            call_selected.setVisibility(View.VISIBLE);
            call_selected.setChecked(false);
        } else {
            call_selected.setChecked(false);
            call_selected.setVisibility(View.GONE);
        }


    }

    private String getLocalFormatterDate(long l) {
        Log.d(TAG, "Long date = " + l);
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String date = dateFormatter.format(new Date(l));
        Log.d(TAG, "Date = " + date);
        return date;
    }

    private static String getTimeString(int duration) {
        int minutes = (int) Math.floor(duration / 1000 / 60);
        int seconds = (duration / 1000) - (minutes * 60);
        return minutes + ":" + String.format("%02d", seconds);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.call_selected:
                RecordsRecyclerListFragment.getInstance().prepareSelection(view, this.getAdapterPosition());
                break;

            case R.id.call_isLove:
                AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(mContext.getContentResolver()) {
                    @Override
                    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                        if (cursor != null) {
                            cursor.moveToFirst();
                            logCursor(cursor);
                            cursor.moveToFirst();
                            String id = cursor.getString(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_ID));
                            int isLove = cursor.getInt(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_IS_LOVE));

                            Log.d(TAG, "call_isLove = " + isLove);
                            int isLoveNew = isLove == 1 ? 0 : 1;
                            if (isLoveNew == 1) {
                                call_isLove.setImageResource(R.drawable.ic_favorite);
                            } else {
                                call_isLove.setImageResource(R.drawable.ic_favorite_border_black);
                            }

                            RecordDbHelper.updateIsLoveColumn(mContext, id, isLoveNew);
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
