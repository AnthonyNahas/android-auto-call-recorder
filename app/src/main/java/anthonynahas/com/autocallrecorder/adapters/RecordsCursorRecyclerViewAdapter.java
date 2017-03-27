package anthonynahas.com.autocallrecorder.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.views.RecordViewHolder;

/**
 * @author Anthony Nahas
 * @version 0.1
 * @since 20.03.2017
 */

public class RecordsCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter {

    private Context mContext;

    public RecordsCursorRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        RecordViewHolder holder = (RecordViewHolder) viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        holder.setData(cursor);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
}
