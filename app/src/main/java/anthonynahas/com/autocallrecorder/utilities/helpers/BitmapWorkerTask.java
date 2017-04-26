package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.activities.MainActivity;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;

/**
 * Created by A on 18.06.16.
 */
public class BitmapWorkerTask extends AsyncTask<Void,Void,Bitmap> {

    WeakReference<ImageView> imageViewWeakReference;
    Context context;
    Cursor cursor;

    public BitmapWorkerTask(ImageView imageView,Context context, Cursor cursor) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap img = ContactHelper.getBitmapForContactID(context.getContentResolver(),1,
                cursor.getLong(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_CONTACT_ID)));
        if(img != null){
            MainActivity.setBitmapToMemoryCache(cursor.getString(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_NUMBER)),img);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(bitmap != null && imageViewWeakReference != null){
            ImageView imageView = imageViewWeakReference.get();
            if(imageView != null){
                imageView.setImageBitmap(bitmap);
            }
        }
        else {
            if(imageViewWeakReference != null){
                ImageView imageView = imageViewWeakReference.get();
                if(imageView != null){
                    imageView.setImageResource(R.drawable.custmtranspprofpic);
                }
            }
        }
    }
}
