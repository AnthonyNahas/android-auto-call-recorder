package anthonynahas.com.autocallrecorder.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.classes.ContactRecord;
import anthonynahas.com.autocallrecorder.classes.Record;

/**
 * Created by anahas on 16.05.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 16.05.2017
 */

public class StatisticRecordsAdapter extends RecyclerView.Adapter<StatisticRecordsAdapter.RecordViewHolder> {

    private static final String TAG = StatisticRecordsAdapter.class.getSimpleName();

    private List<ContactRecord> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mRankTextView;
        private TextView mCallNameOrNumber;


        RecordViewHolder(View view) {
            super(view);

            mRankTextView = (TextView) view.findViewById(R.id.tv_rank);
            mCallNameOrNumber = (TextView) view.findViewById(R.id.tv_call_contact_name_or_number);
        }

    }

    @Override
    public void onViewRecycled(RecordViewHolder holder) {
        super.onViewRecycled(holder);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StatisticRecordsAdapter(List<ContactRecord> mDataset) {
        this.mDataset = mDataset;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_record_card, parent, false);

        return new RecordViewHolder(view);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecordViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        //viewHolder.mRankTextView.setText(mDataset.get(position).getNumber());
        viewHolder.mRankTextView.setText("#" + (position + 1));
        viewHolder.mCallNameOrNumber.setText(mDataset.get(position).getNumber());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
