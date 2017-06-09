package com.anthonynahas.autocallrecorder.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.classes.Res;
import com.anthonynahas.autocallrecorder.utilities.asyncTasks.ContactNameAsyncTask;
import com.anthonynahas.autocallrecorder.utilities.asyncTasks.ContactPhotosAsyncTask;
import com.anthonynahas.autocallrecorder.utilities.helpers.DateTimeHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.MemoryCacheHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;
import com.anthonynahas.ui_animator.CheckboxAnimator;

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
    private int mCounter;
    private boolean actionMode;
    private boolean closeActionMode;
    private List<Record> mRecordsList;
    private DateTimeHelper mDateTimeHelper;
    private PreferenceHelper mPreferenceHelper;

    public RecordsAdapter() {
        actionMode = false;
        closeActionMode = false;
        mCounter = 0;
        mDateTimeHelper = DateTimeHelper.newInstance();

    }

    public RecordsAdapter(List<Record> mRecordsList) {
        this.mRecordsList = mRecordsList;
        mDateTimeHelper = DateTimeHelper.newInstance();
    }

    public boolean isActionMode() {
        return actionMode;
    }

    public void setActionMode(boolean actionMode) {
        this.actionMode = actionMode;
        closeActionMode = !this.actionMode;
        if (!actionMode) {
            clearRecordsSelected();
        }
        notifyDataSetChanged();
    }

    public List<Record> getRecordsList() {
        return mRecordsList;
    }

    public void setRecordsList(List<Record> recordsList) {
        mRecordsList = recordsList;
    }

    public int getCounter() {
        return mCounter;
    }

    public void setCounter(int mCounter) {
        this.mCounter = mCounter;
    }

    public void increaseCounter() {
        mCounter++;
    }

    public void decreaseCounter() {
        mCounter--;
    }

    public void resetCounter() {
        mCounter = 0;
    }

    public void swapData(List<Record> newRecordsList) {
        if (this.mRecordsList == null) {
            if (newRecordsList != null) {
                this.mRecordsList = newRecordsList;
                notifyItemRangeInserted(0, newRecordsList.size());
            }
        } else {
            List<Record> subToAdd = ListUtils.subtract(newRecordsList, this.mRecordsList);
            subToAdd.size();

            if (!subToAdd.isEmpty()) {
                for (Record record : subToAdd) {
                    int index = newRecordsList.indexOf(record);
                    this.mRecordsList.add(index, record);
                    notifyItemInserted(index);
                }
            }

            List<Record> subToDelete = ListUtils.subtract(this.mRecordsList, newRecordsList);
            subToDelete.size();

            if (!subToDelete.isEmpty()) {
                for (Record record : subToDelete) {
                    int index = this.mRecordsList.indexOf(record);
                    this.mRecordsList.remove(index);
                    notifyItemRemoved(index);
                }
            }
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // each data item is just a string in this case
        private CheckBox mCheckBoxCallSelected;
        private TextView mTVCallNameOrNumber;
        private TextView mTVDate;
        private TextView mTVDuration;
        private ImageView mIVProfile;
        private ImageView mIVCallIsIncoming;
        private ImageView mIVCallIsLove;

        RecordViewHolder(View view) {
            super(view);
            mCheckBoxCallSelected = (CheckBox) view.findViewById(R.id.call_selected);
            mCheckBoxCallSelected.setOnClickListener(this);

            mTVCallNameOrNumber = (TextView) view.findViewById(R.id.tv_call_contact_name_or_number);
            mTVDate = (TextView) view.findViewById(R.id.tv_call_date);
            mTVDuration = (TextView) view.findViewById(R.id.tv_call_duration);

            mIVProfile = (ImageView) view.findViewById(R.id.iv_profile);
            mIVCallIsIncoming = (ImageView) view.findViewById(R.id.iv_call_is_incoming);

            mIVCallIsLove = (ImageView) view.findViewById(R.id.iv_call_isLove);
            mIVCallIsLove.setOnClickListener(this);
        }

        private void handleTVCallNameOrNumber(@NonNull RecordViewHolder viewHolder, int position) {
            Record record = mRecordsList.get(position);
            if (position == RecyclerView.NO_POSITION) {
                Log.d(TAG, "no position");
            }
            record.setName(record.getName() != null && !record.getName().isEmpty() ?
                    record.getName()
                    :
                    MemoryCacheHelper.getMemoryCacheForContactsName(record.getNumber()));
            if (record.getName() != null && !record.getName().isEmpty()) {
                viewHolder.mTVCallNameOrNumber.setText(record.getName());
            } else {
                new ContactNameAsyncTask(mContext, record, viewHolder.mTVCallNameOrNumber).execute();
            }
        }

        private void handleIVProfile(@NonNull RecordViewHolder viewHolder, int position) {
            Record record = mRecordsList.get(position);
            if (position == RecyclerView.NO_POSITION) {
                Log.d(TAG, "no position");
            }
            Bitmap cachedBitmap = MemoryCacheHelper.getBitmapFromMemoryCache(record.getNumber());
            if (cachedBitmap != null) {
                viewHolder.mIVProfile.setImageBitmap(cachedBitmap);
            } else {
                new ContactPhotosAsyncTask(mContext, record, viewHolder.mIVProfile).execute(1);
            }
        }

        private void handleIVCallIsLove(int position) {
            if (position != RecyclerView.NO_POSITION) {
                Record record = mRecordsList.get(position);
                record.setLove(mContext, !record.isLove());
                notifyItemChanged(position); //very important -> otherwise the view holder will not update and rebind
            }
        }

        private void handleCheckBoxCallSelected(Boolean isChecked, int position) {
            mRecordsList.get(position).setSelected(isChecked);
            LocalBroadcastManager.getInstance(mContext)
                    .sendBroadcast(new Intent(Res.ACTION_MODE_COUNTER)
                            .putExtra(Res.IS_CHECKED_KEY, !isChecked));
            notifyItemChanged(position); //very important -> otherwise the view holder will not update and rebind
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_call_contact_name_or_number:
                    break;
                case R.id.call_selected:
                    handleCheckBoxCallSelected(mCheckBoxCallSelected.isChecked(), getAdapterPosition());
                    break;
                case R.id.iv_call_isLove:
                    handleIVCallIsLove(getAdapterPosition());
                    break;
            }
        }
    }

    private void clearRecordsSelected() {
        for (Record record : mRecordsList) {
            if (record.isSelected()) {
                record.setSelected(false);
            }
        }
    }

    public void deleteRecordsSelected() {
        PreferenceHelper preferenceHelper = new PreferenceHelper(mContext);
        boolean toRecycleBin = preferenceHelper.toMoveInRecycleBin();
        for (int i = 0, j = mRecordsList.size(); i < j; i++) {
            Record record = mRecordsList.get(i);
            if (record.isSelected()) {
                if (toRecycleBin) {

                } else {

                }
                mRecordsList.remove(i);
                notifyItemRemoved(i);
                j--;
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
    public void onBindViewHolder(final RecordViewHolder viewHolder, int position) {
        Record record = mRecordsList.get(position);

        if (isActionMode()) {
            CheckboxAnimator.newInstance(viewHolder.mCheckBoxCallSelected).toRight();
        } else if (!actionMode && closeActionMode) {
            CheckboxAnimator.newInstance(viewHolder.mCheckBoxCallSelected).toLeft();
        }

        viewHolder
                .mTVCallNameOrNumber
                .setText(record.getNumber());

        viewHolder
                .mTVDate
                .setText(mDateTimeHelper.getLocalFormatterDate(record.getDate()));

        viewHolder
                .mTVDuration
                .setText(mDateTimeHelper.getTimeString(record.getDuration()));

        viewHolder
                .mIVCallIsIncoming
                .setImageResource(record.isIncoming() ? R.drawable.ic_call_received : R.drawable.ic_call_made);

        viewHolder
                .mIVCallIsIncoming
                .setColorFilter(record.isIncoming() ? Color.RED : Color.GREEN);

        viewHolder
                .mIVCallIsLove
                .setImageResource(record.isLove() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border_black);

        viewHolder.handleTVCallNameOrNumber(viewHolder, position);
        viewHolder.handleIVProfile(viewHolder, position);

        if (viewHolder.mCheckBoxCallSelected.isShown()) {
            viewHolder.mCheckBoxCallSelected.setChecked(record.isSelected());
        }
    }

    @Override
    public int getItemCount() {
        if (mRecordsList != null) {
            return mRecordsList.size();
        }
        return -1;
    }
}
