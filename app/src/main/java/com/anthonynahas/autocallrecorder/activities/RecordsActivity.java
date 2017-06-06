package com.anthonynahas.autocallrecorder.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.adapters.RecordsAdapter;
import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.providers.RecordsContentProvider;
import com.anthonynahas.autocallrecorder.utilities.helpers.DialogHelper;
import com.anthonynahas.autocallrecorder.utilities.support.DemoRecordSupport;
import com.anthonynahas.autocallrecorder.utilities.support.ItemClickSupport;
import com.anthonynahas.ui_animator.sample.SampleMainActivity;
import com.arlib.floatingsearchview.FloatingSearchView;

import org.chalup.microorm.MicroOrm;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Activity that displays only rubbished records from the end user.
 * https://stackoverflow.com/jobs/144551/software-engineer-android-centralway-numbrs?med=clc
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 01.06.17
 */

public class RecordsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ItemClickSupport.OnItemClickListener,
        ItemClickSupport.OnItemLongClickListener {

    private static final String TAG = RecordsActivity.class.getSimpleName();

    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecordsAdapter mAdapter;
    private FloatingSearchView mSearchView;
    private Toolbar mToolbar;

    private int mLoaderManagerID;
    private Bundle mArguments;

    private int mCounter;

    public enum args {
        title,
        projection,
        selection,
        selectionArguments,
        limit,
        offset
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        String activityTitle = getIntent().getStringExtra(args.title.name());
        mArguments = prepareArguments(activityTitle);
        mLoaderManagerID = 0;
        mCounter = 0;

        setContentView(R.layout.activity_records);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(activityTitle);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new RecordsAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());

        // TODO: 06.06.2017 on resume ?
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(this);
        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(this);

        // TODO: 02.06.17 refresh on scrolling the recyclerview

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
//        mSearchView.attachNavigationDrawerToMenuButton(mDrawer);
        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            /**
             * Perform an action when a menu item is selected
             *
             * @param item the selected item
             */
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                switch (id) {
                    case R.id.action_add_demo_record:
                        DemoRecordSupport.newInstance().createDemoRecord(getApplicationContext());
                        mAdapter.notifyItemChanged(0);
                        break;
                    case R.id.action_start_sample_animations:
                        startActivity(new Intent(getApplicationContext(), SampleMainActivity.class));
                        break;
                    case R.id.action_sort:
                        DialogHelper.openSortDialog((AppCompatActivity) getApplicationContext());
                        break;
                    case R.id.action_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });

        getSupportLoaderManager().initLoader(mLoaderManagerID, mArguments, this);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        if (mAdapter.isActionMode()) {
            CheckBox call_selected = ((CheckBox) v.findViewById(R.id.call_selected));
            boolean isChecked = call_selected.isChecked();
            call_selected.setChecked(!isChecked);
            mAdapter.getRecordsList().get(position).setSelected(!isChecked);
            if (isChecked) {
                mCounter--;
            } else {
                mCounter++;
            }
            updateToolbar();
        } else {
            // TODO: 06.06.17 open dialog
        }
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        if (!mAdapter.isActionMode()) {
            mAdapter.setActionMode(!mAdapter.isActionMode());
            CheckBox call_selected = ((CheckBox) v.findViewById(R.id.call_selected));
            boolean isChecked = call_selected.isChecked();
            call_selected.setChecked(!isChecked);
            updateToolbar();
            updateToolbarMenu();
        }
        else{
            cancelActionMode();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mAdapter.isActionMode()) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.action_mode_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (mAdapter.isActionMode()) {
                    cancelActionMode();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.isActionMode()) {
            cancelActionMode();
        } else {
            super.onBackPressed();
        }
    }

    private void updateToolbar() {
        if (mAdapter.isActionMode()) {
            if (mCounter == 0) {
                getSupportActionBar().setTitle(getResources().getString(R.string.toolbar_action_mode_text));
            } else {
                getSupportActionBar().setTitle(String.valueOf(mCounter));
            }
        } else {
            getSupportActionBar().setTitle(getIntent().getStringExtra(args.title.name()));
        }
    }

    private void updateToolbarMenu() {
        if (mAdapter.isActionMode()) {
            mToolbar.inflateMenu(R.menu.action_mode_menu);
        } else {
            mToolbar.getMenu().clear();
        }
    }

    private void cancelActionMode() {
        mAdapter.setActionMode(false);
        mCounter = 0;
        updateToolbar();
        updateToolbarMenu();
        mAdapter.notifyDataSetChanged();
        getSupportLoaderManager().restartLoader(mLoaderManagerID, mArguments, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = args.getStringArray(RecordsActivity.args.projection.name());
        String selection = args.getString(RecordsActivity.args.selection.name());
        String[] selectionArgs = args.getStringArray(RecordsActivity.args.selectionArguments.name());
        String sort = null;
        int limit = args.getInt(RecordsActivity.args.limit.name());
        int offset = args.getInt(RecordsActivity.args.offset.name());

        switch (id) {
            case 0:
                Uri uri = RecordDbContract.CONTENT_URL
                        .buildUpon()
                        .appendQueryParameter(RecordsContentProvider.QUERY_PARAMETER_LIMIT,
                                String.valueOf(limit))
                        .appendQueryParameter(RecordsContentProvider.QUERY_PARAMETER_OFFSET,
                                String.valueOf(offset))
                        .build();
                return new CursorLoader(this, uri, projection, selection, selectionArgs, sort);

            default:
                throw new IllegalArgumentException("no loader id handled!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapData((new MicroOrm().listFromCursor(data, Record.class)));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO: 02.06.2017
    }

    private Bundle prepareArguments(String activityTitle) {
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        int limit = 15; //default
        int offset = 0; //default

        Bundle args = new Bundle();

        if (getResources().getString(R.string.title_activity_rubbished_records).equals(activityTitle)) {
            projection = new String[]{"*"};
            selection = RecordDbContract.RecordItem.COLUMN_IS_TO_DELETE + " = 1";
        } else if (getResources().getString(R.string.title_activity_locked_records).equals(activityTitle)) {
            projection = new String[]{"*"};
            //selection = RecordDbContract.RecordItem.COLUMN_IS_LOCKED + " = 1";
        }

        args.putStringArray(RecordsActivity.args.projection.name(), projection);
        args.putString(RecordsActivity.args.selection.name(), selection);
        args.putStringArray(RecordsActivity.args.selectionArguments.name(), selectionArgs);
        args.putInt(RecordsActivity.args.limit.name(), limit);
        args.putInt(RecordsActivity.args.offset.name(), offset);

        return args;

    }

    public void refreshCursorLoader(int newOffset) {
        mArguments.putInt(RecordsActivity.args.offset.name(), newOffset);
        getSupportLoaderManager().restartLoader(mLoaderManagerID, mArguments, this);
    }

}
