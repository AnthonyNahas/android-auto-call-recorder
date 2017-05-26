package anthonynahas.com.autocallrecorder.utilities.asyncTasks;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.adapters.StatisticRecordsAdapter;
import anthonynahas.com.autocallrecorder.utilities.helpers.ContactHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.ImageHelper;

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

    private Context mContext;
    private StatisticRecordsAdapter mStatisticRecordsAdapter;
    private StatisticRecordsAdapter.RecordViewHolder mViewHolder;
    private int mPosition;

    public ContactPhotosAsyncTask(Context context, StatisticRecordsAdapter statisticRecordsAdapter, StatisticRecordsAdapter.RecordViewHolder viewHolder, int position) {
        mContext = context;
        mStatisticRecordsAdapter = statisticRecordsAdapter;
        mViewHolder = viewHolder;
        mPosition = position;
    }

    @Override
    protected Bitmap doInBackground(Long... longs) {
        return ContactHelper.getBitmapForContactID(mContext.getContentResolver(), 0, longs[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mViewHolder.getImageProfile().setImageBitmap(bitmap != null ?
                bitmap : ImageHelper.decodeSampledBitmapFromResource(mContext.getResources(),
                R.drawable.custmtranspprofpic60px, 60, 60));
        mStatisticRecordsAdapter.bindViewHolder(mViewHolder, mPosition);
    }
}
