package com.anthonynahas.autocallrecorder.utilities.asyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.models.Record;
import com.anthonynahas.autocallrecorder.utilities.helpers.ImageHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.MemoryCacheHelper;

/**
 * Created by A on 25.05.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 25.05.17
 */
@Deprecated
/**
 * Class that tries to load a bitmap of contact by id asynchronously
 */
public class ContactPhotosAsyncTask extends AsyncTask<Integer, Void, Bitmap> {

    private static final String TAG = ContactPhotosAsyncTask.class.getSimpleName();

    private static int sCounter = 1;


    private Context mContext;
    private Record mRecord;
    private ImageView mImageView;
    private ImageHelper mImageHelper;
    private MemoryCacheHelper mMemoryCacheHelper;

    public ContactPhotosAsyncTask(@NonNull Context context,
                                  @NonNull Record record,
                                  @NonNull ImageView imageView,
                                  MemoryCacheHelper mMemoryCacheHelper) {
        mContext = context;
        mRecord = record;
        mImageView = imageView;
        this.mMemoryCacheHelper = mMemoryCacheHelper;

        if (mImageHelper == null) {
            mImageHelper = new ImageHelper(mContext.getResources());
        }
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "started with " + sCounter);
    }

    /**
     * Try to load a bitmap for a specific contact by id,
     * supporting loading large and thumbnails bitmap
     *
     * @param integers - whether in large mode (0) or thumbnails (1)
     * @return - the bitmap of the contact if it's available
     */
    @Override
    protected Bitmap doInBackground(Integer... integers) {
        Bitmap contactBitmap;
        switch (integers[0]) {
            case 0:
//                contactBitmap = ContactHelper.getBitmapForContactID(mContext.getContentResolver(),
//                        integers[0], mRecord.getContactID());
//                return contactBitmap != null ?
//                        contactBitmap
//                        :
//                        mImageHelper.decodeSampledBitmapFromResource(R.drawable.custmtranspprofpic, 150, 150);

            case 1:
//                Bitmap cachedBitmap = mMemoryCacheHelper.getBitmapFromMemoryCache(mRecord.getNumber());
//                contactBitmap = cachedBitmap != null ?
//                        cachedBitmap
//                        :
//                        ContactHelper.getBitmapForContactID(mContext.getContentResolver(),
//                                integers[0], mRecord.getContactID());
//                if (cachedBitmap == null && contactBitmap != null) {
//                    mMemoryCacheHelper.setBitmapToMemoryCache(mRecord.getNumber(), contactBitmap);
//                    return contactBitmap;
                }

                return mImageHelper.decodeSampledBitmapFromResource(R.drawable.custmtranspprofpic60px, 60, 60);
//            default:
//                return null;
//        }

    }

    /**
     * When done with background works, set th
     *
     * @param bitmap
     */
    @Override
    protected void onPostExecute(@NonNull final Bitmap bitmap) {
        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
            }
        }.postDelayed(new Runnable() {
            @Override
            public void run() {
                mImageView.setImageBitmap(bitmap);
                Log.d(TAG, "done " + sCounter);
            }
        }, 100 * sCounter++);

    }
}
