package anthonynahas.com.autocallrecorder.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import anthonynahas.com.autocallrecorder.R;

/**
 * Created by anahas on 16.05.2017.
 */

public class StatisticRecordsAdapter extends RecyclerView.Adapter<StatisticRecordsAdapter.RecordViewHolder> {

    private static final String TAG = StatisticRecordsAdapter.class.getSimpleName();

    private View mRootView;
    private String[] mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public RecordViewHolder(View view){
            super(view);

            mTextView = (TextView) view.findViewById(R.id.info_text);
        }

        public RecordViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    @Override
    public void onViewRecycled(RecordViewHolder holder) {
        super.onViewRecycled(holder);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StatisticRecordsAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecordViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        if (mRootView == null) {
            mRootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        }
        // set the view's size, margins, paddings and layout parameters
            TextView t = (TextView) mRootView.findViewById(R.id.info_text);
            RecordViewHolder vh = new RecordViewHolder(t);
            return vh;

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecordViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
