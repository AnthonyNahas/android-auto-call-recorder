package anthonynahas.com.autocallrecorder.activities;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.adapters.RecordsCursorRecyclerViewAdapter;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;
import anthonynahas.com.autocallrecorder.providers.RecordsContentProvider;

//import android.content.Loader;

/**
 * Created by A on 20.03.17.
 */

public class RecordsTestActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = RecordsTestActivity.class.getSimpleName();

    private Context mContext;
    private SharedPreferences mSharedPreferences;


    public final int offset = 30;
    private int page = 0;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private boolean loadingMore = false;
    private Toast shortToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_test_records);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);



        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        RecordsCursorRecyclerViewAdapter mAdapter = new RecordsCursorRecyclerViewAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        //getLoaderManager().initLoader(0, null, this);


        int itemsCountLocal = getItemsCountLocal();
        if (itemsCountLocal == 0) {
            fillTestElements();
        }

        shortToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int maxPositions = layoutManager.getItemCount();

                if (lastVisibleItemPosition == maxPositions - 1) {
                    if (loadingMore)
                        return;

                    loadingMore = true;
                    page++;
                    refresh();
                }
            }
        });

        getLoaderManager().restartLoader(0, null, this);

    }

    public void refresh(){
        getLoaderManager().restartLoader(0, null, this);
    }

    private void fillTestElements() {
        int size = 1;
        ContentValues[] cvArray = new ContentValues[size];
        for (int i = 0; i < cvArray.length; i++) {
            ContentValues cv = new ContentValues();
            //cv.put(TableItems.TEXT, ("text " + i));
            cvArray[i] = cv;
        }

        getContentResolver().bulkInsert(RecordsContentProvider.urlForItems(0), cvArray);
    }

    private int getItemsCountLocal() {
        int itemsCount = 0;

        Cursor query = getContentResolver().query(RecordsContentProvider.urlForItems(0), null, null, null, null);
        if (query != null) {
            itemsCount = query.getCount();
            query.close();
        }
        return itemsCount;
    }

    /*
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = null;
        if (args != null) {
            String searchRecord = args.getString("SEARCH_KEY");
            if (searchRecord != null && searchRecord.length() > 0) {
                selection = RecordDbContract.RecordItem.COLUMN_NUMBER + " LIKE '%" + searchRecord + "%'";
            }
        }

        String sort = mSharedPreferences.getString(SettingsActivity.KEY_SORT_SELECTION, RecordDbContract.RecordItem.COLUMN_DATE)
                + mSharedPreferences.getString(SettingsActivity.KEY_SORT_ARRANGE, " DESC");
        return new CursorLoader(this, RecordDbContract.CONTENT_URL, projectALL, selection, null, sort);
    }
    */
    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projectALL = new String[]{"*"};
        switch (id) {
            case 0:
                return new CursorLoader(this,
                        RecordsContentProvider.urlForItems(offset * page), projectALL, null, null, null);
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }


    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 0:
                Log.d(TAG, "onLoadFinished: loading MORE");
                shortToast.setText("loading MORE " + page);
                shortToast.show();

                Cursor cursor =
                        ((RecordsCursorRecyclerViewAdapter) mRecyclerView.getAdapter()).getCursor();

                //fill all exisitng in adapter
                MatrixCursor mx = new MatrixCursor(RecordDbContract.RecordItem.ALL_COLUMNS);
                fillMx(cursor, mx);

                //fill with additional result
                fillMx(data, mx);

                ((RecordsCursorRecyclerViewAdapter) mRecyclerView.getAdapter()).swapCursor(mx);


                handlerToWait.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingMore = false;
                    }
                }, 2000);

                break;
            default:
                throw new IllegalArgumentException("no loader id handled!");
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }


    private Handler handlerToWait = new Handler();

    private void fillMx(Cursor data, MatrixCursor mx) {
        if (data == null)
            return;

        data.moveToPosition(-1);
        while (data.moveToNext()) {
            mx.addRow(new Object[]{
                    data.getString(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID)),
                    data.getString(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER)),
                    data.getLong(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_CONTACTID)),
                    data.getLong(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE)),
                    data.getInt(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_SIZE)),
                    data.getInt(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION)),
                    data.getInt(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_INCOMING))
            });
        }
    }
}
