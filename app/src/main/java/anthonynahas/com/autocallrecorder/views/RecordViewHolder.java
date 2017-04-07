package anthonynahas.com.autocallrecorder.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;
import anthonynahas.com.autocallrecorder.utilities.helpers.ContactHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.ImageHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.MemoryCacheHelper;

/**
 * Created by A on 20.03.17.
 */

public class RecordViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = RecordViewHolder.class.getSimpleName();

    private Context mContext;

    private ImageView call_contact_profile;
    private TextView call_contact_number_or_name;
    private TextView call_date;
    private ImageView call_icon_isIncoming;
    private TextView call_duration;


    public RecordViewHolder(Context context, View view) {
        super(view);
        mContext = context;
        call_contact_profile = (ImageView) view.findViewById(R.id.img_profile);
        call_contact_number_or_name = (TextView) view.findViewById(R.id.call_contact_name_number);
        call_date = (TextView) view.findViewById(R.id.call_date);
        call_icon_isIncoming = (ImageView) view.findViewById(R.id.call_icon_isIncoming);
        call_duration = (TextView) view.findViewById(R.id.call_duration);
    }

    public void setData(Cursor cursor) {
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
                    String contactName = ContactHelper.getContacName(mContext.getContentResolver(), phoneNumber);
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
            call_contact_number_or_name.setText("Unkown");
            //call_contact_number_or_name.setText(phoneNumber);
        } else {
            call_contact_number_or_name.setText(contact_number_or_name);
        }


        switch (cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_INCOMING))) {
            case 0:
                call_icon_isIncoming.setBackgroundColor(Color.GREEN);
                break;
            case 1:
                call_icon_isIncoming.setBackgroundColor(Color.RED);
                break;
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
            Bitmap img = ContactHelper.getBitmapForContactID(mContext.getContentResolver(), 1,
                    cursor.getLong(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_CONTACTID)));
            if (img != null) {
                call_contact_profile.setImageBitmap(img);
                MemoryCacheHelper.setBitmapToMemoryCache(phoneNumber, img);
            } else {
                //viewHolder.call_contact_profile.setImageResource(R.drawable.custmtranspprofpic);
                call_contact_profile.setImageBitmap(ImageHelper.decodeSampledBitmapFromResource(mContext.getResources(), R.drawable.custmtranspprofpic60px, 60, 60));
            }
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

}
