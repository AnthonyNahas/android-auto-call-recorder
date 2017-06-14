package com.anthonynahas.autocallrecorder.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.adapters.RecordsAdapter;
import com.anthonynahas.autocallrecorder.models.Record;
import com.anthonynahas.autocallrecorder.classes.Res;
import com.anthonynahas.autocallrecorder.fragments.dialogs.InputDialog;
import com.anthonynahas.autocallrecorder.fragments.dialogs.RecordsDialog;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.providers.RecordsContentProvider;
import com.anthonynahas.autocallrecorder.utilities.helpers.ContactHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.DialogHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.FileHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.SQLiteHelper;
import com.anthonynahas.autocallrecorder.utilities.support.ActionModeSupport;
import com.anthonynahas.autocallrecorder.utilities.support.ItemClickSupport;
import com.anthonynahas.autocallrecorder.views.managers.WrapContentLinearLayoutManager;
import com.anthonynahas.ui_animator.sample.SampleMainActivity;
import com.arlib.floatingsearchview.FloatingSearchView;

import org.chalup.microorm.MicroOrm;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
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
        ItemClickSupport.OnItemLongClickListener,
        FloatingSearchView.OnQueryChangeListener,
        FloatingSearchView.OnMenuItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RecordsActivity.class.getSimpleName();

    @Inject
    FileHelper mFileHelper;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.floating_search_view)
    FloatingSearchView searchView;

    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    @BindView(R.id.content_loading_progressbar)
    ContentLoadingProgressBar contentLoadingProgressBar;

    private SwipeRefreshLayout mSwipeContainer;

    private Toolbar mToolbar;
    private Context mContext;
    private Activity mAppCompatActivity;
    private RecordsAdapter mAdapter;

    private ActionModeSupport mActionModeSupport;
    private BroadcastReceiver mBroadcastReceiver;

    private int mLoaderManagerID;
    private Bundle mArguments;
    private Handler mHandlerToWait;
    private PreferenceHelper mPreferenceHelper;

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
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        ButterKnife.bind(this);

        mContext = this;
        mAppCompatActivity = this;
        String activityTitle = getIntent().getStringExtra(args.title.name());
        mArguments = prepareArguments(activityTitle);
        mPreferenceHelper = new PreferenceHelper(this);
        mHandlerToWait = new Handler();
        mLoaderManagerID = 0;


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(activityTitle);
        }

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Res.ACTION_MODE_COUNTER:
                        mActionModeSupport.updateToolbarCounter(intent.getBooleanExtra(Res.IS_CHECKED_KEY, false));
                        break;
                    default:
                        break;
                }
            }
        };

        // Lookup the swipe container view
//        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
//        mSwipeContainer.setOnRefreshListener(this);
//        // Configure the refreshing colors
//        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        // specify an adapter (see also next example)
        mAdapter = new RecordsAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.getItemAnimator().setAddDuration(Res.RECYCLER_VIEW_ANIMATION_DELAY);
        recyclerView.getItemAnimator().setRemoveDuration(Res.RECYCLER_VIEW_ANIMATION_DELAY);
        recyclerView.getItemAnimator().setMoveDuration(Res.RECYCLER_VIEW_ANIMATION_DELAY);
        recyclerView.getItemAnimator().setChangeDuration(Res.RECYCLER_VIEW_ANIMATION_DELAY);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(this);

        // TODO: 02.06.17 refresh on scrolling the recyclerview
        searchView.setOnQueryChangeListener(this);
        searchView.setOnMenuItemClickListener(this);

        mActionModeSupport = new ActionModeSupport(
                getIntent().getStringExtra(args.title.name()),
                false,
                this,
                getSupportActionBar(),
                mToolbar,
                mAdapter);

        refreshCursorLoader();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, new IntentFilter(Res.ACTION_MODE_COUNTER));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, final View v) {
        if (mAdapter.isActionMode()) {
            mActionModeSupport.handleCheckBoxSelectionInActionMode(position, v);
        } else {
            RecordsDialog.show(mContext, mAdapter.getRecordsList().get(position));
        }
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        mActionModeSupport.enterActionMode(position, v);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mActionModeSupport.inflateMenu(getMenuInflater(), menu, R.menu.action_mode_menu);
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

            case R.id.menu_action_delete:
                mAdapter.deleteRecordsSelected();
                cancelActionMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cancelActionMode() {
        mActionModeSupport.cancelActionMode();
        refreshCursorLoader();
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.isActionMode()) {
            cancelActionMode();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        refreshCursorLoader();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (!progressBar.isShown()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        String[] projection = args.getStringArray(RecordsActivity.args.projection.name());
        String selection = args.getString(RecordsActivity.args.selection.name());
        String[] selectionArgs = args.getStringArray(RecordsActivity.args.selectionArguments.name());
        String sort = mPreferenceHelper.getSortSelection()
                + mPreferenceHelper.getSortArrange();
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

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapData((new MicroOrm().listFromCursor(data, Record.class)));
        mHandlerToWait.postDelayed(new Runnable() {
            @Override
            public void run() {
//                onLoadingMore = false;
                contentLoadingProgressBar.hide();
                progressBar.setVisibility(View.GONE);
//                mSwipeContainer.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mAdapter.swapData(new ArrayList<Record>());
    }

    /**
     * Perform an action when a menu item is selected from the
     * floating search view
     *
     * @param item the selected item
     */
    @Override
    public void onActionMenuItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_add_demo_record:
                InputDialog.newInstance().show(mAppCompatActivity, "Contact ID");
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

    @Override
    public void onSearchTextChanged(String oldQuery, String newQuery) {
        Log.d(TAG, "oldQuery = " + oldQuery + " | newQuery = " + newQuery);

        String contactIDsArguments = SQLiteHelper
                .convertArrayToInOperatorArguments(ContactHelper
                        .getContactIDsByName(ContactHelper
                                .getContactCursorByName(mContext
                                        .getContentResolver(), newQuery)));

        String selection = RecordDbContract.RecordItem.COLUMN_NUMBER
                + " LIKE ?"
                + " OR "
                + RecordDbContract.RecordItem.COLUMN_CONTACT_ID
                + " IN "
                + contactIDsArguments;
        //+ "= 682";
        String[] selectionArgs = new String[]{"%" + newQuery + "%"};

        Bundle args = (Bundle) mArguments.clone();
        args.putString(RecordsActivity.args.selection.name(), selection);
        args.putStringArray(RecordsActivity.args.selectionArguments.name(), selectionArgs);

        refreshCursorLoader(args);
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

    private void refreshCursorLoader() {
        getSupportLoaderManager().restartLoader(mLoaderManagerID, mArguments, this);
    }

    private void refreshCursorLoader(Bundle args) {
        contentLoadingProgressBar.postInvalidate();
        android.widget.ProgressBar bar = new android.widget.ProgressBar(this);
        bar.setIndeterminate(true);
        getSupportLoaderManager().restartLoader(mLoaderManagerID, args, this);
    }

    public void refreshCursorLoader(int newOffset) {
        mArguments.putInt(RecordsActivity.args.offset.name(), newOffset);
        getSupportLoaderManager().restartLoader(mLoaderManagerID, mArguments, this);
    }

}
