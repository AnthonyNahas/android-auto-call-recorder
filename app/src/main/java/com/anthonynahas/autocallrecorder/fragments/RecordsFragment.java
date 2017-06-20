package com.anthonynahas.autocallrecorder.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.activities.RecordsActivity;
import com.anthonynahas.autocallrecorder.adapters.RecordsAdapter;
import com.anthonynahas.autocallrecorder.configurations.Constant;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.fragments.RecordsFragmentKey;
import com.anthonynahas.autocallrecorder.events.search.OnQueryChangedEvent;
import com.anthonynahas.autocallrecorder.events.tabs.OnTabSelected;
import com.anthonynahas.autocallrecorder.models.Record;
import com.anthonynahas.autocallrecorder.fragments.dialogs.RecordsDialog;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.providers.RecordsContentProvider;
import com.anthonynahas.autocallrecorder.utilities.helpers.FileHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;
import com.anthonynahas.autocallrecorder.utilities.support.ActionModeSupport;
import com.anthonynahas.autocallrecorder.utilities.support.ItemClickSupport;
import com.anthonynahas.autocallrecorder.views.managers.WrapContentLinearLayoutManager;

import org.chalup.microorm.MicroOrm;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static android.view.View.GONE;

/**
 * Created by anahas on 09.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 09.06.17
 */

public class RecordsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ItemClickSupport.OnItemClickListener,
        ItemClickSupport.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RecordsFragment.class.getSimpleName();

    private static RecordsFragment sRecordFragment;

    public boolean hasFocus;
    public int position;
    public Bundle args;

    @Inject
    RecordsAdapter mAdapter;

    @Inject
    RecordsDialog mRecordsDialog;

    @Inject
    @RecordsFragmentKey
    ActionModeSupport mActionModeSupport;

    @Inject
    PreferenceHelper mPreferenceHelper;

    @Inject
    FileHelper mFileHelper;

    @Inject
    Constant mConstant;

    @Inject
    EventBus mEventBus;

    @BindView(R.id.content_loading_progressbar)
    ContentLoadingProgressBar contentLoadingProgressBar;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar_action_mode)
    Toolbar toolbar;

    @BindView(R.id.counter_text)
    TextView tv_counter;

    @BindView(R.id.button_action_mode_delete)
    Button b_delete;

    private Unbinder mUnbinder;
    private Context mContext;
    private BroadcastReceiver mActionModeBroadcastReceiver;


    private Handler mHandlerToWait;

    private boolean onLoadingMore = false;
    private int mOffset = 0;
    private final int mLimit = 30;
    private int mLoaderManagerID = 0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecordsListFragment.
     */
    public synchronized static RecordsFragment getInstance() {
        if (sRecordFragment == null) {
            sRecordFragment = new RecordsFragment();
        }
        return sRecordFragment;
    }

    public static RecordsFragment newInstance() {
        return new RecordsFragment();
    }

    public static RecordsFragment newInstance(Bundle args) {
        RecordsFragment recordsFragment = new RecordsFragment();
        recordsFragment.setArguments(args);
        return recordsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            this.args = args;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recrods_card_list, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mContext = view.getContext();
        mHandlerToWait = new Handler();

        // Lookup the swipe container view
        swipeContainer.setOnRefreshListener(this);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.getItemAnimator().setAddDuration(mConstant.RECYCLER_VIEW_ANIMATION_DELAY);
        recyclerView.getItemAnimator().setRemoveDuration(mConstant.RECYCLER_VIEW_ANIMATION_DELAY);
        recyclerView.getItemAnimator().setMoveDuration(mConstant.RECYCLER_VIEW_ANIMATION_DELAY);
        recyclerView.getItemAnimator().setChangeDuration(mConstant.RECYCLER_VIEW_ANIMATION_DELAY);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(this);
        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(this);

        toolbar.setVisibility(GONE);

//        mActionModeSupport = new ActionModeSupport("test", true, mContext, toolbar, mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int maxPositions = layoutManager.getItemCount();

                if (lastVisibleItemPosition == maxPositions - 1
                        && maxPositions * mOffset == mLimit * mOffset
                        && mOffset != 0) {
                    if (onLoadingMore) {
                        return;
                    }

                    onLoadingMore = true;
                    mOffset++;
//                    refresh();
                }
            }
        });

        mActionModeSupport.setAdapter(mAdapter);
        mActionModeSupport.setToolbar(toolbar);

        getLoaderManager().initLoader(mLoaderManagerID, args, this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        mEventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mEventBus.unregister(this);
    }

    // When binding a fragment in onCreateView, set the views to null in onDestroyView.
    // ButterKnife returns an Unbinder on the initial binding that has an unbind method to do this automatically.
    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "on activity created");
        mActionModeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String senderClassName = intent.getStringExtra(mConstant.ACTION_MODE_SENDER);
                boolean isInActionMode = intent.getBooleanExtra(mConstant.ACTION_MODE_SATE, false);
                if (isInActionMode) {
//                    mActionModeSupport.enterActionMode(); //no need
                } else {
//                    mActionModeSupport.cancelActionMode();
                }
            }
        };

        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mActionModeBroadcastReceiver,
                        new IntentFilter(mConstant.BROADCAST_ACTION_ON_ACTION_MODE));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mActionModeBroadcastReceiver);
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        if (mAdapter.isActionMode()) {
            mActionModeSupport.handleCheckBoxSelectionInActionMode(position, v);
        } else {
            Bundle args = new Bundle();
            args.putParcelable(mConstant.REC_PARC_KEY, mAdapter.getRecordsList().get(position));
            mRecordsDialog.show(getActivity().getSupportFragmentManager(), RecordsDialog.TAG);
        }
    }

    @Override
    public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
        mActionModeSupport.enterActionMode(position, v);
        return false;
    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(mLoaderManagerID, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        contentLoadingProgressBar.show();

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
                return new CursorLoader(mContext, uri, projection, selection, selectionArgs, sort);

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
                onLoadingMore = false;
                contentLoadingProgressBar.hide();
                swipeContainer.setRefreshing(false);
            }
        }, 2000);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        mAdapter.swapData(new ArrayList<Record>());
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onQueryChangedEvent(OnQueryChangedEvent event) {
//        if (hasFocus) {
            refreshCursorLoader(event.args);
//        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onTabSelected(OnTabSelected event) {
        hasFocus = event.position == position;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onTabUnselected(OnTabSelected event) {
        hasFocus = event.position == position;
    }

    private void refreshCursorLoader(Bundle args) {
        getLoaderManager().restartLoader(mLoaderManagerID, args, this);
    }

    private void cancelActionMode() {
        mActionModeSupport.cancelActionMode();
        getLoaderManager().restartLoader(mLoaderManagerID, args, this);
    }
}
