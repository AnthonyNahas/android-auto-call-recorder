package com.anthonynahas.autocallrecorder.utilities.asyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.adapters.StatisticRecordsAdapter;
import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.utilities.helpers.ContactHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.ImageHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.MemoryCacheHelper;

/**
 * Created by A on 25.05.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 25.05.17
 */

/**
 * Class that tries to load a bitmap of contact by id asynchronously
 */
public class ContactPhotosAsyncTask extends AsyncTask<Long, Void, Bitmap> {

    private static final String TAG = ContactPhotosAsyncTask.class.getSimpleName();

    private static int sCounter = 1;

    private Context mContext;
    private Record mRecord;
    private ImageView mImageView;
    private StatisticRecordsAdapter.RecordViewHolder mViewHolder;

    public ContactPhotosAsyncTask(Context context, Record record, ImageView imageView) {
        mContext = context;
        mRecord = record;
        mImageView = imageView;
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "started with " + sCounter);
    }

    @Override
    protected Bitmap doInBackground(Long... longs) {
        Bitmap cachedBitmap = MemoryCacheHelper.getBitmapFromMemoryCache(mRecord.getNumber());
        Bitmap contactsBitmap = cachedBitmap != null ?
                cachedBitmap
                :
                ContactHelper.getBitmapForContactID(mContext.getContentResolver(), 1, longs[0]);
        if (cachedBitmap == null && contactsBitmap != null) {
            MemoryCacheHelper.setBitmapToMemoryCache(mRecord.getNumber(), contactsBitmap);
        }
        return contactsBitmap;
    }

    @Override
    protected void onPostExecute(final Bitmap bitmap) {

        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
            }
        }.postDelayed(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageBitmap(bitmap != null ?
                        bitmap : ImageHelper.decodeSampledBitmapFromResource(mContext.getResources(),
                        R.drawable.custmtranspprofpic60px, 60, 60));
                Log.d(TAG, "done " + sCounter);
            }
        }, 1000 * sCounter++);

    }
}
