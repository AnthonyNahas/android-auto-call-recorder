package anthonynahas.com.autocallrecorder.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.MatrixCursor;
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
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.adapters.RecordsCursorRecyclerViewAdapter;
import anthonynahas.com.autocallrecorder.classes.Resources;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;
import anthonynahas.com.autocallrecorder.providers.RecordsContentProvider;
import anthonynahas.com.autocallrecorder.providers.RecordsQueryHandler;
import anthonynahas.com.autocallrecorder.utilities.decoraters.ItemClickSupport;
import anthonynahas.com.autocallrecorder.utilities.helpers.ContactHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.DialogHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.PreferenceHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.SQLiteHelper;
import jp.wasabeef.recyclerview.animators.BaseItemAnimator;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;
import jp.wasabeef.recyclerview.animators.FadeInRightAnimator;
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator;
import jp.wasabeef.recyclerview.animators.FlipInLeftYAnimator;
import jp.wasabeef.recyclerview.animators.FlipInRightYAnimator;
import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import jp.wasabeef.recyclerview.animators.OvershootInRightAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInBottomAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInLeftAnimator;
import jp.wasabeef.recyclerview.animators.ScaleInTopAnimator;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static anthonynahas.com.autocallrecorder.R.id.recycler_view;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordsRecyclerListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordsRecyclerListFragment#getInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Anthony Nahas
 * @version 1.4.0
 * @since 29.03.2017
 */
public class RecordsRecyclerListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        FloatingSearchView.OnQueryChangeListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RecordsRecyclerListFragment.class.getSimpleName();

    private static RecordsRecyclerListFragment sFragment;


    public int sCounter = 0;
    private int mOffset = 0;
    private final int mLimit = 30;
    private final int mLoaderManagerID = 0;

    private Bundle mArgs = new Bundle();
    private String mSelection = null;
    private String[] mSelectionArgs = null;

    private Handler mHandlerToWait = new Handler();

    private Context mContext;
    private RecyclerView mRecyclerView;
    private PreferenceHelper mPreferenceHelper;
    private RecordsCursorRecyclerViewAdapter mAdapter;

    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private SwipeRefreshLayout mSwipeContainer;
    private boolean onLoadingMore = false;
    private Toast shortToast;

    private Toolbar mToolbar;
    private TextView mCounterTV;
    private Button mDeleteButton;
    public static boolean sIsInActionMode = false;
    private BroadcastReceiver mActionModeBroadcastReceiver;

    private OnFragmentInteractionListener mListener;

    private enum Type {
        FadeIn(new FadeInAnimator()),//0
        FadeInDown(new FadeInDownAnimator()),//1
        FadeInUp(new FadeInUpAnimator()),//2
        FadeInLeft(new FadeInLeftAnimator()),//3
        FadeInRight(new FadeInRightAnimator()),//4
        Landing(new LandingAnimator()),//5
        ScaleIn(new ScaleInAnimator()),//6
        ScaleInTop(new ScaleInTopAnimator()),//7
        ScaleInBottom(new ScaleInBottomAnimator()),//8
        ScaleInLeft(new ScaleInLeftAnimator()),//19
        FlipInTopX(new FlipInTopXAnimator()),//10
        FlipInBottomX(new FlipInBottomXAnimator()),//11
        FlipInLeftY(new FlipInLeftYAnimator()),//12
        FlipInRightY(new FlipInRightYAnimator()),//13
        SlideInLeft(new SlideInLeftAnimator()),//14
        SlideInRight(new SlideInRightAnimator()),//15
        SlideInDown(new SlideInDownAnimator()),//16
        SlideInUp(new SlideInUpAnimator()),//17
        OvershootInRight(new OvershootInRightAnimator(1.0f)),//18
        OvershootInLeft(new OvershootInLeftAnimator(1.0f));//19

        private BaseItemAnimator mAnimator;

        Type(BaseItemAnimator animator) {
            mAnimator = animator;
        }

        public BaseItemAnimator getAnimator() {
            return mAnimator;
        }
    }

    public enum BundleArgs {
        mode,
        search,
        searchKey,
        selection,
        selectionArgs
    }


    public RecordsRecyclerListFragment() {
        Log.d(TAG, "on new RecordsRecyclerListFragment Instance");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecordsRecyclerListFragment.
     */
    public synchronized static RecordsRecyclerListFragment getInstance() { // TODO: 05.05.2017 : 2 Instance pro app - get or new instance
        if (sFragment == null) {
            sFragment = new RecordsRecyclerListFragment();
        }
        return sFragment;
    }

    public static RecordsRecyclerListFragment newInstance() {
        return new RecordsRecyclerListFragment();
    }

    public static RecordsRecyclerListFragment newInstance(Bundle args) {
        RecordsRecyclerListFragment recordsRecyclerListFragment = new RecordsRecyclerListFragment();
        recordsRecyclerListFragment.setArguments(args);
        return recordsRecyclerListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            mArgs = args;
            //mSelection = args.getString(BundleArgs.selection.name());
            //mSelectionArgs = args.getStringArray(BundleArgs.selection.name());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_recrods_card_list, container, false);
        mContext = mView.getContext();
        mPreferenceHelper = new PreferenceHelper(mContext);

        mContentLoadingProgressBar = (ContentLoadingProgressBar) mView.findViewById(R.id.content_loading_progressbar);
        // Lookup the swipe container view
        mSwipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_container);
        mRecyclerView = (RecyclerView) mView.findViewById(recycler_view);

        mToolbar = (Toolbar) mView.findViewById(R.id.toolbar_action_mode);
        mToolbar.setVisibility(GONE);
        mCounterTV = (TextView) mView.findViewById(R.id.counter_text);
        mDeleteButton = (Button) mView.findViewById(R.id.button_action_mode_delete);
        updateToolbarText();


        mRecyclerView.setItemAnimator(Type.values()[4].getAnimator());
        //mRecyclerView.getItemAnimator().setAddDuration(500);
        mRecyclerView.getItemAnimator().setRemoveDuration(500);
        //mRecyclerView.setItemAnimator(new SlideInLeftAnimator()); // https://github.com/wasabeef/recyclerview-animators
        //SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        //mRecyclerView.setItemAnimator(animator);
        //mAdapter.notifyItemRemoved();

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecordsCursorRecyclerViewAdapter(mContext, null);
        mRecyclerView.setAdapter(mAdapter);

        RecordsQueryHandler.getInstance(mContext.getContentResolver()).setAdapter(mAdapter);
        mSwipeContainer.setOnRefreshListener(this);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "position = " + position);
                        if (sIsInActionMode) {
                            //Cursor cursor = mAdapter.getCursor();
                            //cursor.moveToPosition(position);
                            AppCompatCheckBox call_selected = ((AppCompatCheckBox) v.findViewById(R.id.call_selected));
                            boolean isChecked = call_selected.isChecked();
                            if (isChecked) {
                                sCounter--;
                            } else {
                                sCounter++;
                            }
                            call_selected.setChecked(!isChecked);
                            updateToolbarText();
                        } else {
                            mAdapter.getItemId(position);
                            openRecordDialog(v, position);
                        }
                    }
                }
        );

        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
                if (sIsInActionMode) {
                    return false;
                }
                //handleActionMode(false);
                notifyOnActionMode(true);
                return false;
            }
        });

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //int itemsCountLocal = getItemsCountLocal();
        //if (itemsCountLocal == 0) {
        //fillTestElements();
        //}

        shortToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    refresh();
                }
            }
        });

        getLoaderManager().initLoader(mLoaderManagerID, mArgs, this);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "on activity created");
        mActionModeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String senderClassName = intent.getStringExtra(Resources.ACTION_MODE_SENDER);
                boolean isInActionMode = intent.getBooleanExtra(Resources.ACTION_MODE_SATE, false);
                handleActionMode(isInActionMode);
                Log.d(TAG, "on receive action mode: " + isInActionMode + " from --> " + senderClassName);
            }
        };

        LocalBroadcastManager.getInstance(mContext)
                .registerReceiver(mActionModeBroadcastReceiver,
                        new IntentFilter(Resources.BROADCAST_ACTION_ON_ACTION_MODE));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mActionModeBroadcastReceiver);

    }

    public void handleActionMode(boolean isInActionMode) {
        if (!isInActionMode) {
            //leave action mode
            mToolbar.getMenu().clear();
            mToolbar.setVisibility(GONE);
            mCounterTV.setVisibility(GONE);
            sIsInActionMode = false;
        } else {
            //enter action mode
            mToolbar.getMenu().clear();
            //mToolbar.inflateMenu(R.menu.action_mode_menu);
            mToolbar.setVisibility(VISIBLE);
            mCounterTV.setVisibility(VISIBLE);
            sIsInActionMode = true;
        }

        refresh();
    }


    public void prepareSelection(View view, int position) {
        if (((AppCompatCheckBox) view).isChecked()) {
            ++sCounter;
        } else {
            --sCounter;
        }
        updateToolbarText();
    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "on refresh after swipe");
        hardResetLoader();
    }

    /**
     * Refresh the cursor loader
     */
    public void refresh() {
        Log.d(TAG, "onRefresh()");
        getLoaderManager().restartLoader(mLoaderManagerID, mArgs, this);
    }


    public void hardResetLoader() {
        mOffset = 0;
        mAdapter = new RecordsCursorRecyclerViewAdapter(mContext, null);
        mRecyclerView.setAdapter(mAdapter);
        getLoaderManager().restartLoader(mLoaderManagerID, mArgs, this);
    }

    /**
     * Refresh the cursor loader when the resultcode ok is from the dialog helper
     * as well as the the request code is the right one.
     *
     * @param requestCode - the used request code
     * @param resultCode  - whether the result is ok or has been canceled
     * @param data        - returned data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // resultCode = 0 if canceled and -1 if ok :=)
        //Toast.makeText(getActivity(), "Result: " + resultCode, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "req code = " + requestCode + "  | resultCode = " + requestCode);
        if (requestCode == DialogHelper.REQUEST_CODE_FOR_SORT_DIALOG
                && resultCode == Activity.RESULT_OK) {
            hardResetLoader();
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }

    private int getItemsCountLocal() {
        int itemsCount = 0;

        Cursor query = mContext.getContentResolver().query(RecordsContentProvider
                .urlForItems(0), null, null, null, null);
        if (query != null) {
            itemsCount = query.getCount();
            query.close();
        }
        return itemsCount;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        mContentLoadingProgressBar.show();
        String[] projection = new String[]{"*"};
        String selection = null;
        String[] selectionArgs = null;

        if (args != null) {
            String mode = args.getString(BundleArgs.mode.name());
            if (mode != null && mode.equals(BundleArgs.search.name())) {
                String searchKey = args.getString(BundleArgs.searchKey.name());
                if (searchKey != null && !searchKey.isEmpty() && searchKey.length() > 0) {
                    //selection = RecordDbContract.RecordItem.COLUMN_NUMBER + " LIKE '%" + mSearchKey + "%'";
                    String contactIDsArguments = SQLiteHelper
                            .convertArrayToInOperatorArguments(ContactHelper
                                    .getContactIDsByName(ContactHelper
                                            .getContactCursorByName(mContext
                                                    .getContentResolver(), searchKey)));
                    selection = RecordDbContract.RecordItem.COLUMN_NUMBER
                            + " LIKE ?"
                            + " OR "
                            + RecordDbContract.RecordItem.COLUMN_CONTACT_ID
                            + " IN "
                            + contactIDsArguments;
                    //+ "= 682";
                    selectionArgs = new String[]{"%" + searchKey + "%"};
                    mOffset = 0; // TODO: 04.05.17 replace mOffset with mLimit
                }
            }
            String loveSelection = args.getString(BundleArgs.selection.name());
            if (loveSelection != null && !loveSelection.isEmpty() && loveSelection.length() > 0) {
                if (selection != null && !selection.isEmpty()) {
                    //selection += " AND ";
                } else {
                    selection = loveSelection;
                }
            }
        }

        String sort = mPreferenceHelper.getSortSelection()
                + mPreferenceHelper.getSortArrange();

        switch (id) {
            case 0:
                //Uri uri = RecordsContentProvider.urlForItems(mLimit * mOffset);
                Uri uri = RecordDbContract.CONTENT_URL
                        .buildUpon()
                        .appendQueryParameter(RecordsContentProvider.QUERY_PARAMETER_LIMIT,
                                String.valueOf(mLimit))
                        .appendQueryParameter(RecordsContentProvider.QUERY_PARAMETER_OFFSET,
                                String.valueOf(mOffset))
                        //.encodedQuery("mLimit=" + mLimit + "," + mOffset)
                        .build();
                return new CursorLoader(getActivity(),
                        uri, projection, selection, selectionArgs, sort);
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadingFinished");
        switch (loader.getId()) {
            case 0:
                Log.d(TAG, "onLoadFinished: loading MORE");
                if (onLoadingMore) {
                    mergeCursor(data);
                } else {
                    reloadCursor(data);
                }
                break;
            default:
                throw new IllegalArgumentException("no loader id handled!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "on loader reset");
        mAdapter.changeCursor(null);
    }

    private void fillMx(Cursor data, MatrixCursor mx) {
        if (data == null)
            return;

        data.moveToPosition(-1);
        while (data.moveToNext()) {
            mx.addRow(new Object[]{
                    data.getString(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID)),
                    data.getString(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER)),
                    data.getLong(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_CONTACT_ID)),
                    data.getLong(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE)),
                    data.getInt(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_SIZE)),
                    data.getInt(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION)),
                    data.getInt(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_INCOMING)),
                    data.getInt(data.getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_LOVE))
            });
        }
    }

    private void reloadCursor(Cursor data) {
        data.moveToFirst();
        mAdapter = new RecordsCursorRecyclerViewAdapter(mContext, data);
        mRecyclerView.setAdapter(mAdapter);
        mContentLoadingProgressBar.hide();
        mSwipeContainer.setRefreshing(false);
        //mAdapter.notifyItemInserted(0);
    }

    private void mergeCursor(Cursor data) {
        shortToast.setText("loading MORE " + mOffset);
        shortToast.show();
        Cursor cursor =
                ((RecordsCursorRecyclerViewAdapter) mRecyclerView.getAdapter()).getCursor();

        //fill all existing in adapter
        MatrixCursor mx = new MatrixCursor(RecordDbContract.RecordItem.ALL_COLUMNS);
        fillMx(cursor, mx);

        //fill with additional result
        fillMx(data, mx);

        ((RecordsCursorRecyclerViewAdapter) mRecyclerView.getAdapter()).swapCursor(mx);


        mHandlerToWait.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoadingMore = false;
                mContentLoadingProgressBar.hide();
                mSwipeContainer.setRefreshing(false);
            }
        }, 2000);
    }

    private void openRecordDialog(View view, int position) {
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        Log.d(TAG, "list-position = " + position);

        Bundle args = new Bundle();
        String contact_number_or_name = ((TextView) view.findViewById(R.id.call_contact_name_number)).getText().toString();
        args.putString(RecordsDialogFragment.NUMBER_CN_KEY, contact_number_or_name);
        args.putString(RecordsDialogFragment.NUMBER_KEY, cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER)));
        args.putString(RecordsDialogFragment.REC_AUDIO_ID_KEY, cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID)));
        args.putInt(RecordsDialogFragment.REC_DURATION_KEY, cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION)));
        args.putLong(RecordsDialogFragment.REC_CONTACT_ID_KEY, cursor.getLong(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_CONTACT_ID)));
        args.putInt(RecordsDialogFragment.REC_DURATION_KEY, cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_INCOMING)));
        RecordsDialogFragment recordsDialogFragment = new RecordsDialogFragment();
        recordsDialogFragment.setArguments(args);
        recordsDialogFragment.show(((Activity) mContext).getFragmentManager(), RecordsDialogFragment.TAG);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
            super.onAttach(context);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSearchTextChanged(String oldQuery, String newQuery) {
        Log.d(TAG, "oldQuery = " + oldQuery + " | newQuery = " + newQuery);
        ContactHelper.getContactCursorByName(mContext.getContentResolver(), newQuery);

        mArgs.putString(BundleArgs.mode.name(), BundleArgs.search.name());
        mArgs.putString(BundleArgs.searchKey.name(), newQuery);

        getLoaderManager().restartLoader(mLoaderManagerID, mArgs, this);
        //hardResetLoader();
    }

    private void notifyOnActionMode(boolean state) {
        Intent intent = new Intent(Resources.BROADCAST_ACTION_ON_ACTION_MODE);
        intent.putExtra(Resources.ACTION_MODE_SENDER, RecordsRecyclerListFragment.class.getSimpleName());
        intent.putExtra(Resources.ACTION_MODE_SATE, state);
        LocalBroadcastManager.getInstance(mContext)
                .sendBroadcast(intent);
        sIsInActionMode = true;
        //mAdapter.notifyItemChanged(0);
    }

    private void updateToolbarText() {
        if (sCounter == 0) {
            mDeleteButton.setVisibility(GONE);
            mCounterTV.setText(getResources().getString(R.string.toolbar_action_mode_text));
        } else {
            mCounterTV.setText(String.valueOf(sCounter));
            if (mDeleteButton.getVisibility() == GONE) {
                mDeleteButton.setVisibility(VISIBLE);
            }
        }
    }

    public FloatingSearchView.OnQueryChangeListener getOnQueryChangeListener() {
        return this;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
