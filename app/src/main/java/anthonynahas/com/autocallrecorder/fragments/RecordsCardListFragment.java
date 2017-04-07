package anthonynahas.com.autocallrecorder.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.activities.SettingsActivity;
import anthonynahas.com.autocallrecorder.adapters.RecordsCursorRecyclerViewAdapter;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;
import anthonynahas.com.autocallrecorder.providers.RecordsContentProvider;
import anthonynahas.com.autocallrecorder.utilities.decoraters.ItemClickSupport;
import anthonynahas.com.autocallrecorder.utilities.helpers.DialogHelper;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static anthonynahas.com.autocallrecorder.R.id.recyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordsCardListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordsCardListFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * @author Anthony Nahas
 * @version 0.1
 * @since 29.03.2017
 */
public class RecordsCardListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        FloatingSearchView.OnQueryChangeListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = RecordsCardListFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public final int offset = 30;
    private int page = 0;
    private String mSearchKey = "";

    private View mView;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SharedPreferences mSharedPreferences;
    private RecordsCursorRecyclerViewAdapter mAdapter;
    private ContentLoadingProgressBar mContentLoadingProgressBar;
    private SwipeRefreshLayout mSwipeContainer;
    private boolean loadingMore = false;
    private Toast shortToast;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RecordsCardListFragment() {
        Log.d(TAG, "on new RecordsCardListFragment");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordsCardListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordsCardListFragment newInstance(String param1, String param2) {
        RecordsCardListFragment fragment = new RecordsCardListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_recrods_card_list, container, false);
        mContext = mView.getContext();
        mContentLoadingProgressBar = (ContentLoadingProgressBar) mView.findViewById(R.id.content_loading_progressbar);
        // Lookup the swipe container view
        mSwipeContainer = (SwipeRefreshLayout) mView.findViewById(R.id.swipeContainer);
        mRecyclerView = (RecyclerView) mView.findViewById(recyclerView);

        mRecyclerView.setItemAnimator(new SlideInLeftAnimator());
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        mRecyclerView.setItemAnimator(animator);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecordsCursorRecyclerViewAdapter(mContext, null);
        mRecyclerView.setAdapter(mAdapter);
        mSwipeContainer.setOnRefreshListener(this);

        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.d(TAG, "position = " + position);
                    }
                }
        );

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        int itemsCountLocal = getItemsCountLocal();
        if (itemsCountLocal == 0) {
            //fillTestElements();
        }

        shortToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);

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

        return mView;
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
        getLoaderManager().restartLoader(0, null, this);
    }


    public void hardResetLoader() {
        mAdapter = new RecordsCursorRecyclerViewAdapter(mContext, null);
        mRecyclerView.setAdapter(mAdapter);
        getLoaderManager().restartLoader(0, null, this);
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

    private void fillTestElements() {
        int size = 1;
        ContentValues[] cvArray = new ContentValues[size];
        for (int i = 0; i < cvArray.length; i++) {
            ContentValues cv = new ContentValues();
            //cv.put(TableItems.TEXT, ("text " + i));
            cvArray[i] = cv;
        }

        mContext.getContentResolver().bulkInsert(RecordsContentProvider.urlForItems(0), cvArray);
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
        if (!mSearchKey.isEmpty() && mSearchKey.length() > 0) {
            //selection = RecordDbContract.RecordItem.COLUMN_NUMBER + " LIKE '%" + mSearchKey + "%'";
            selection = RecordDbContract.RecordItem.COLUMN_NUMBER + " LIKE ?";
            selectionArgs = new String[]{"%" + mSearchKey + "%"};
            page = 0;
        }
        String sort = mSharedPreferences.getString(SettingsActivity.KEY_SORT_SELECTION,
                RecordDbContract.RecordItem.COLUMN_DATE)
                + mSharedPreferences.getString(SettingsActivity.KEY_SORT_ARRANGE, " DESC");

        switch (id) {
            case 0:
                return new CursorLoader(getActivity(),
                        RecordsContentProvider.urlForItems(offset * page), projection, selection, selectionArgs, sort);
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 0:
                Log.d(TAG, "onLoadFinished: loading MORE");
                shortToast.setText("loading MORE " + page);
                shortToast.show();

                Cursor cursor =
                        ((RecordsCursorRecyclerViewAdapter) mRecyclerView.getAdapter()).getCursor();

                //fill all existng in adapter
                MatrixCursor mx = new MatrixCursor(RecordDbContract.RecordItem.ALL_COLUMNS);
                fillMx(cursor, mx);

                //fill with additional result
                fillMx(data, mx);

                ((RecordsCursorRecyclerViewAdapter) mRecyclerView.getAdapter()).swapCursor(mx);


                handlerToWait.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingMore = false;
                        mContentLoadingProgressBar.hide();
                        mSwipeContainer.setRefreshing(false);
                    }
                }, 2000);

                break;
            default:
                throw new IllegalArgumentException("no loader id handled!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "on loader reset");
        //mRecordsCursorAdapter.changeCursor(null);
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
        mSearchKey = newQuery;
        hardResetLoader();
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
