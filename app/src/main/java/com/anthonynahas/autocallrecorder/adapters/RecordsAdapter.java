package com.anthonynahas.autocallrecorder.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.utilities.helpers.DateTimeHelper;

import org.apache.commons.collections4.ListUtils;

import java.util.List;

/**
 * Created by anahas on 02.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 02.06.17
 */

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordViewHolder> {

    private static final String TAG = RecordsAdapter.class.getSimpleName();

    private Context mContext;
    private DateTimeHelper mDateTimeHelper;

    private List<Record> mRecordsList;

    public RecordsAdapter() {
        mDateTimeHelper = DateTimeHelper.newInstance();
    }

    public RecordsAdapter(List<Record> mRecordsList) {
        this.mRecordsList = mRecordsList;
        mDateTimeHelper = DateTimeHelper.newInstance();
    }

    public void swapData(List<Record> mRecordsList) {
        if (this.mRecordsList == null) {
            if (mRecordsList != null) {
                this.mRecordsList = mRecordsList;
                notifyDataSetChanged();
                notifyItemRangeChanged(0, mRecordsList.size());
            }
        } else {
            List<Record> subToAdd = ListUtils.subtract(mRecordsList, this.mRecordsList);
            subToAdd.size();

            if (!subToAdd.isEmpty()) {
                for (Record record : subToAdd) {
                    mRecordsList.add(0, record);
                    notifyItemRangeChanged(1, mRecordsList.size());
                    notifyItemInserted(0);
                    notifyDataSetChanged();
                }
            }

            List<Record> subToDelete = ListUtils.subtract(this.mRecordsList, mRecordsList);
            subToDelete.size();

            if (!subToDelete.isEmpty()) {
                for (Record record : subToDelete) {
                    int index = mRecordsList.indexOf(record);
                    mRecordsList.remove(index);
                    notifyItemRemoved(index);
                }
            }
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // each data item is just a string in this case
        private AppCompatCheckBox mCheckBoxCallSelected;
        private TextView mTVCallNameOrNumber;
        private TextView mTVDate;
        private TextView mTVDuration;
        private ImageView mIVProfile;
        private ImageView mIVCallIsIncoming;
        private ImageView mIVCallIsLove;

        RecordViewHolder(View view) {
            super(view);
            mCheckBoxCallSelected = (AppCompatCheckBox) view.findViewById(R.id.call_selected);
            mTVCallNameOrNumber = (TextView) view.findViewById(R.id.tv_call_contact_name_or_number);
            mTVDate = (TextView) view.findViewById(R.id.tv_call_date);
            mTVDuration = (TextView) view.findViewById(R.id.tv_call_duration);
            mIVProfile = (ImageView) view.findViewById(R.id.iv_profile);
            mIVCallIsIncoming = (ImageView) view.findViewById(R.id.iv_call_is_incoming);
            mIVCallIsLove = (ImageView) view.findViewById(R.id.iv_call_isLove);
        }


        public ImageView getImageProfile() {
            return mIVProfile;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.tv_call_contact_name_or_number:
                    break;
                case R.id.call_selected:
                    break;
                case R.id.iv_call_isLove:
                    break;
            }
        }

    }

    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_card, parent, false);
        mContext = view.getContext();
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordViewHolder viewHolder, int position) {
        Record record = mRecordsList.get(position);

        viewHolder
                .mTVCallNameOrNumber
                .setText(record.getNumber()); // TODO: 02.06.2017 get name from contact api async

        viewHolder
                .mTVDate
                .setText(mDateTimeHelper.getLocalFormatterDate(record.getDate()));

        viewHolder
                .mTVDuration
                .setText(mDateTimeHelper.getTimeString(record.getDuration()));

        viewHolder
                .mIVCallIsIncoming
                .setImageResource(record.isIsIncoming() ? R.drawable.ic_call_received : R.drawable.ic_call_made);

        viewHolder
                .mIVCallIsIncoming
                .setColorFilter(record.isIsIncoming() ? Color.RED : Color.GREEN);

        viewHolder
                .mIVCallIsLove
                .setImageResource(record.isIsLove() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border_black);

    }

    @Override
    public int getItemCount() {
        if (mRecordsList != null) {
            return mRecordsList.size();
        }
        return -1;
    }
}
