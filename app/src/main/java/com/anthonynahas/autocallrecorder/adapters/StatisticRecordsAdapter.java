package com.anthonynahas.autocallrecorder.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.classes.RecordExtended;
import com.anthonynahas.autocallrecorder.utilities.asyncTasks.ContactPhotosAsyncTask;
import com.anthonynahas.autocallrecorder.utilities.helpers.MemoryCacheHelper;

import java.util.List;

/**
 * Created by anahas on 16.05.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 16.05.2017
 */

public class StatisticRecordsAdapter extends RecyclerView.Adapter<StatisticRecordsAdapter.RecordViewHolder> {

    private static final String TAG = StatisticRecordsAdapter.class.getSimpleName();

    private Context mContext;
    private List<RecordExtended> mRecordsList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mTVCallNameOrNumber;
        private TextView mTVRank;
        private TextView mTVTotalIncomingCalls;
        private TextView mTVTotalOutgoingCalls;
        private ImageView mIVProfile;


        RecordViewHolder(View view) {
            super(view);
            mTVCallNameOrNumber = (TextView) view.findViewById(R.id.tv_call_contact_name_or_number);
            mTVRank = (TextView) view.findViewById(R.id.tv_rank);
            mTVTotalIncomingCalls = (TextView) view.findViewById(R.id.tv_total_incoming_calls);
            mTVTotalOutgoingCalls = (TextView) view.findViewById(R.id.tv_total_outgoing_calls);
            mIVProfile = (ImageView) view.findViewById(R.id.iv_profile);

        }
    }

    @Override
    public void onViewRecycled(RecordViewHolder holder) {
        super.onViewRecycled(holder);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StatisticRecordsAdapter(List<RecordExtended> mRecordsList) {
        this.mRecordsList = mRecordsList;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public Record getItem(int position) {
        return mRecordsList.get(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_record_card, parent, false);
        mContext = view.getContext();
        return new RecordViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecordViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Record record = mRecordsList.get(position);
        record.setRank(position + 1);

        viewHolder.mTVRank.setText("#" + record.getRank());
        viewHolder.mTVCallNameOrNumber.setText(record.getNumber());
        viewHolder.mTVTotalIncomingCalls.setText(String.valueOf(record.getTotalIncomingCalls()));
        viewHolder.mTVTotalOutgoingCalls.setText(String.valueOf(record.getTotalOutgoingCall()));

        Bitmap cachedBitmap = MemoryCacheHelper.getBitmapFromMemoryCache(record.getNumber());
        if (cachedBitmap != null) {
            viewHolder.mIVProfile.setImageBitmap(cachedBitmap);
        } else {
            new ContactPhotosAsyncTask(mContext, record, viewHolder.mIVProfile).execute(mRecordsList.get(position).getContactID());
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mRecordsList != null) {
            return mRecordsList.size();
        }
        return -1;
    }

}
