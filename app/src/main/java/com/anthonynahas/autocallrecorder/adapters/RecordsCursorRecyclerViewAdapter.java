package com.anthonynahas.autocallrecorder.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.views.RecordViewHolder;

/**
 * @author Anthony Nahas
 * @version 1.0
 * @since 20.03.2017
 */

public class RecordsCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter {

    private static final String TAG = RecordsCursorRecyclerViewAdapter.class.getSimpleName();

    private Context mContext;

    public RecordsCursorRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public long getItemId(int position) {
        /*
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        String id = cursor.getString(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_ID));
        Log.d(TAG, "id = " + id);
        */
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.record_card, parent, false);
        return new RecordViewHolder(mContext, view);
    }

    @Override
    public void bindViewHolder(RecyclerView.ViewHolder viewHolder) {
        // TODO: 02.05.2017
        RecordViewHolder holder = (RecordViewHolder) viewHolder;
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
