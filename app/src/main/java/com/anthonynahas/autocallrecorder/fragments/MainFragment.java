package com.anthonynahas.autocallrecorder.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.adapters.RecordsCursorAdapter;
import com.anthonynahas.autocallrecorder.configurations.Constant;
import com.anthonynahas.autocallrecorder.fragments.dialogs.RecordsDialog;
import com.anthonynahas.autocallrecorder.models.Record;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.utilities.asyncTasks.AudioFileAsyncTask;
import com.anthonynahas.autocallrecorder.utilities.helpers.FileHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

/**
 * Created by A on 25.04.16.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 25.04.2016
 */
@Deprecated
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @Inject
    Context mContext;

    @Inject
    Constant mConstant;

    @Inject
    FileHelper mFileHelper;

    private View v;
    private ListView mRecListView;
    private PreferenceHelper mPreferenceHelper;

    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mFloatingActionButton;
    private RecordsCursorAdapter mRecordsCursorAdapter;
    private static final int LOADER_ID = 0;
    private ArrayList<String> mSelectedRecItems;
    private ArrayList<Long> mSelectedRecItemsById;
    private SearchItemReceiver mSearchItemReceiver;
    private ProgressDialog mProgressDialog;
    private AsyncTask<ArrayList<String>, Void, Void> mDeleteFilesTask;

    private static final String TAG = MainFragment.class.getSimpleName();
    private static final int REQUEST_CODE_FOR_SORT_DIALOG = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);

        setHasOptionsMenu(true);
        mCoordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.coordinator);
        mRecListView = (ListView) v.findViewById(R.id.listView_records_main);

        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.floating_action_button_search);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert mRecListView != null;
                if (mRecListView.getCount() > 0) {
                    mRecListView.setItemChecked(0, true);
                    ProgressDialog s = new ProgressDialog(getActivity());
                    s.setMessage("Test");
                    //s.show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            getResources().getString(R.string.tst_list_empty), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Register broadcast receiver
        mSearchItemReceiver = new SearchItemReceiver();
        IntentFilter filter = new IntentFilter("SEARCH_ACTION");
        getActivity().getApplicationContext().registerReceiver(mSearchItemReceiver, filter);

        mRecListView.setOnItemClickListener(mOnItemClickListener);

        assert mRecListView != null;

        mRecListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = mRecListView.getCheckedItemCount();
                mode.setTitle(checkedCount + "");
                long Audio_ID = getIDfromCursorsPosition(position);
                String filePath = getAudioFilePath(String.valueOf(Audio_ID));
                if (mRecListView.isItemChecked(position) && !mSelectedRecItems.contains(filePath)) {
                    Log.d(TAG, "pos " + position);

                    if (filePath != null && filePath.length() > 0) {
                        mSelectedRecItems.add(filePath);
                        mSelectedRecItemsById.add(Audio_ID);
                    }
                    Log.d(TAG, "size on checked = " + mSelectedRecItems.size());
                } else {
                    if (!mRecListView.isItemChecked(position) && mSelectedRecItems.contains(filePath)) {
                        mSelectedRecItems.remove(filePath);
                        mSelectedRecItemsById.remove(Audio_ID);
                        Log.d(TAG, "size on checked = " + mSelectedRecItems.size());
                    }
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
                return true;

            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                mSelectedRecItems = new ArrayList<>();
                mSelectedRecItemsById = new ArrayList<>();
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menu_action_delete:
                        mDeleteFilesTask.execute(mSelectedRecItems);
                        for (Long id : mSelectedRecItemsById) {
                            getActivity().getContentResolver().delete(RecordDbContract.CONTENT_URL, RecordDbContract.RecordItem.COLUMN_ID + "= '" + id + "'", null);
                        }
                        //showSnackbar();
                        // Close CAB
                        mode.finish();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });


        mRecListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mRecListView.setItemsCanFocus(false);

        //initialize the loader
        getLoaderManager().initLoader(LOADER_ID, null, this);

        //((MainOldActivity)getActivity()).checkPermission(Manifest.permission.READ_CONTACTS, MainOldActivity.MY_PERMISSIONS_REQUEST_CONTACTS)

        mDeleteFilesTask = new AsyncTask<ArrayList<String>, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setMessage(getResources().getString(R.string.pd_msg));
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(ArrayList<String>... params) {
                ArrayList<String> paths = params[0];

                int size = paths.size();
                int left = 0;

                mProgressDialog.setMessage(getResources().getString(R.string.pd_msg) + left + "/" + size + getResources().getString(R.string.pd_deleted));


                for (String path : paths) {
                    try {
                        File f = new File(path);
                        if (f.delete()) {
                            left++;
                            mProgressDialog.setMessage(getResources().getString(R.string.pd_msg) + left + "/" + size + getResources().getString(R.string.pd_deleted));
                        }
                    } catch (Exception e) {
                        Log.e("AsyncTask", "Error with file deleting", e);
                    }

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //mProgressDialog.dismiss();
            }
        };

        return v;
    }

    @Override
    public void onDestroy() {
        getActivity().getApplicationContext().unregisterReceiver(mSearchItemReceiver);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Do something that differs the Activity's menu here
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                Log.d(TAG, "MenuItem = sort");
                //SortDialog sortDialogFragment = new SortDialog();
                //sortDialogFragment.setTargetFragment(MainFragment.this, REQUEST_CODE_FOR_SORT_DIALOG);
                //sortDialogFragment.show(getFragmentManager(), "sort dialog");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // resultCode = 0 if canceled and -1 if ok :=)
        //Toast.makeText(getActivity(), "Result: " + resultCode, Toast.LENGTH_SHORT).show();
        if (requestCode == REQUEST_CODE_FOR_SORT_DIALOG && resultCode == Activity.RESULT_OK) {
            refresh();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSnackbar() {
        Snackbar snackbar = Snackbar
                .make(mCoordinatorLayout, "test test", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar snackbar1 = Snackbar.make(mCoordinatorLayout, "Message is restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.RED);

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }


    private int convert_dp_To_px(int dp) {
        Resources r = this.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics()
        );
    }

    //call backs for the loader
    /*
    * A CursorLoader runs an asynchronous query in the background against a ContentProvider,
     * and returns the results to the Activity or FragmentActivity from which it was called.
     * */

    /**
     * create new cursor with specific projection
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projectALL = new String[]{"*"};

        String selection = null;
        if (args != null) {
            String searchRecord = args.getString("SEARCH_KEY");
            if (searchRecord != null && searchRecord.length() > 0) {
                selection = RecordDbContract.RecordItem.COLUMN_NUMBER + " LIKE '%" + searchRecord + "%'";
            }
        }

        String sort = mPreferenceHelper.getSortSelection()
                + mPreferenceHelper.getSortArrange();
        return new CursorLoader(getActivity(), RecordDbContract.CONTENT_URL, projectALL, selection, null, sort);
    }

    /**
     * @param loader
     * @param data   data that will be loaded
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //mRecordsCursorAdapter.changeCursor(data);
        data.moveToFirst();
        mRecordsCursorAdapter = new RecordsCursorAdapter(getActivity().getApplicationContext(), data, 0);
        mRecListView.setAdapter(mRecordsCursorAdapter);
    }

    /**
     * called when the last cursor provided to onLoadFinished() is about to be cloused
     *
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecordsCursorAdapter.changeCursor(null);
    }

    private void refresh() {
        Log.d(TAG, "onRefresh()");
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    private void refresh(Bundle args) {
        Log.d(TAG, "onRefresh()");
        getLoaderManager().restartLoader(LOADER_ID, args, this);
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Cursor cursor = mRecordsCursorAdapter.getCursor();
//            cursor.moveToPosition(position);
//            Log.d(TAG, "list-position = " + position);
//
//            Bundle args = new Bundle();
//            Record record = Record.newInstance(cursor);
//            record.setName(((TextView) view.findViewById(R.id.tv_call_contact_name_or_number)).getText().toString());
//            args.putParcelable(mConstant.REC_PARC_KEY, record);
//            RecordsDialog recordsDialog = new RecordsDialog();
//            recordsDialog.setArguments(args);
//            recordsDialog.show(((AppCompatActivity) mContext).getSupportFragmentManager(), RecordsDialog.TAG);
        }
    };

    private void deleteRecord(long id) {
        //getActivity().getContentResolver().delete(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),null,null);
        //getActivity().getContentResolver().delete(ContentUris.withAppendedId(RecordDbContract.CONTENT_URL, id),null,null);
    }

    private Long getIDfromCursorsPosition(int position) {
        Cursor cursor = mRecordsCursorAdapter.getCursor();
        cursor.moveToPosition(position);

        return Long.valueOf(cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID)));
    }

    private String getAudioFilePath(String id) {
        AudioFileAsyncTask audioFileAsyncTask = new AudioFileAsyncTask(id, getActivity().getApplicationContext());
        String path = "";
        try {
            Cursor audioCursor = audioFileAsyncTask.execute().get();
            String id2 = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media._ID));
            path = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA));

            Log.d(TAG, "id original = " + id);
            Log.d(TAG, " id = " + id2 + " | data = " + path);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.e(TAG, "Error : ", e);
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "Error: IndexOutOfBound", e);
        }
        return path;
    }


    public class SearchItemReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //refresh();
            Log.d(TAG, "onReceive");
            Bundle args = intent.getExtras();
            if (args != null) {
                refresh(args);
            }
        }
    }

}
