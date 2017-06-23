package com.anthonynahas.autocallrecorder.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.activities.deprecated.SettingsActivity;
import com.anthonynahas.autocallrecorder.configurations.Constant;
import com.anthonynahas.autocallrecorder.dagger.annotations.android.HandlerToWaitForLoading;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.activities.MainActivityKey;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.fragments.RecordsFragementsLoveKey;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.fragments.RecordsFragmentsMainKey;
import com.anthonynahas.autocallrecorder.events.loading.OnLoadingBegin;
import com.anthonynahas.autocallrecorder.events.loading.OnLoadingDone;
import com.anthonynahas.autocallrecorder.events.tabs.OnTabSelected;
import com.anthonynahas.autocallrecorder.events.tabs.OnTabUnSelected;
import com.anthonynahas.autocallrecorder.fragments.RecordsFragment;
import com.anthonynahas.autocallrecorder.fragments.dialogs.InputDialog;
import com.anthonynahas.autocallrecorder.interfaces.Loadable;
import com.anthonynahas.autocallrecorder.listeners.SearchListener;
import com.anthonynahas.autocallrecorder.utilities.helpers.DialogHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PermissionsHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;
import com.anthonynahas.autocallrecorder.utilities.support.ActionModeSupport;
import com.anthonynahas.ui_animator.sample.SampleMainActivity;
import com.arlib.floatingsearchview.FloatingSearchView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

public class MainActivity extends AppActivity implements
        Loadable,
        NavigationView.OnNavigationItemSelectedListener,
        TabLayout.OnTabSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static boolean sIsInActionMode = false;

    private Activity mAppCompatActivity;

    @Inject
    @RecordsFragmentsMainKey
    RecordsFragment mMainFragment;

    @Inject
    @RecordsFragementsLoveKey
    RecordsFragment mLoveFragment;

    @Inject
    SearchListener mSearchListener;

    @Inject
    InputDialog mInputDialog;

    @Inject
    PreferenceHelper mPreferenceHelper;

    @Inject
    PermissionsHelper mPermissionsHelper;

    @Inject
    DialogHelper mDialogHelper;

    @Inject
    Constant mConstant;

    @Inject
    @MainActivityKey
    ActionModeSupport mActionModeSupport;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout mTabs;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @BindView(R.id.floating_search_view)
    FloatingSearchView mSearchView;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.fab_go_in_action_mode)
    FloatingActionButton mFAB_ActionMode;

    @StringRes
    int mNavDrawerOpen = R.string.navigation_drawer_open;

    @StringRes
    int mNavDrawerClose = R.string.navigation_drawer_close;

    /**
     * Notify all receiver that the app is going in action mode
     */
    @OnClick(R.id.fab_go_in_action_mode)
    protected void notifyOnActionMode() {
        sIsInActionMode = !sIsInActionMode;

        Intent intent = new Intent(mConstant.BROADCAST_ACTION_ON_ACTION_MODE);
        intent.putExtra(mConstant.ACTION_MODE_SENDER, MainActivity.class.getSimpleName());
        intent.putExtra(mConstant.ACTION_MODE_SATE, sIsInActionMode);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

//    @BindView(R.id.toolbar)
//    Toolbar toolbar;

    private SwitchCompat mSwitch_auto_rec;
    private int mCurrentFragmentPosition;
    private BroadcastReceiver mActionModeBroadcastReceiver;
    private FloatingSearchView.OnQueryChangeListener mOnQueryChangeListener;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);
        setContentView(R.layout.activity_main_navigation_drawer_tabs);

        ButterKnife.bind(this);

        mAppCompatActivity = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabs.setupWithViewPager(mViewPager);
        mTabs.addOnTabSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, mNavDrawerOpen, mNavDrawerClose);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        final AppCompatActivity appCompatActivity = this;

        mSearchView.attachNavigationDrawerToMenuButton(mDrawer);
        mSearchView.setOnQueryChangeListener(mSearchListener.init(mMainFragment.getArguments()));
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
                        mInputDialog.show(mAppCompatActivity, "Contact ID");
                        break;
                    case R.id.action_start_sample_animations:
                        startActivity(new Intent(getApplicationContext(), SampleMainActivity.class));
                        break;
                    case R.id.action_sort:
                        mDialogHelper.openSortDialog(appCompatActivity, mSectionsPagerAdapter.getItem(0));
                        break;
                    case R.id.action_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });

        mNavigationView.setNavigationItemSelectedListener(this);
        Menu menu = mNavigationView.getMenu();
        MenuItem menuItem_switch_auto_rec = menu.findItem(R.id.nav_switch_auto_rec);
        View actionView = MenuItemCompat.getActionView(menuItem_switch_auto_rec);
        mSwitch_auto_rec = (SwitchCompat) actionView;

        if (mSwitch_auto_rec != null) {
            mSwitch_auto_rec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, "autorecord = " + mSwitch_auto_rec.isChecked());
                    mPreferenceHelper.setCanAutoRecord(isChecked);
                }
            });
        }

        mActionModeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String senderClassName = intent.getStringExtra(mConstant.ACTION_MODE_SENDER);
                boolean isInActionMode = intent.getBooleanExtra(mConstant.ACTION_MODE_SATE, false);
                handleActionMode(isInActionMode);
                Log.d(TAG, "on receive action mode: " + isInActionMode + " from --> " + senderClassName);
            }
        };

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mActionModeBroadcastReceiver,
                        new IntentFilter(mConstant.BROADCAST_ACTION_ON_ACTION_MODE));
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mEventBus.register(this);
        // requesting required permission on run time
        mPermissionsHelper.requestAllPermissions(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " AUDIO SOURCE = " + mPreferenceHelper.getAudioSource());

        if (mSwitch_auto_rec != null) {
            mSwitch_auto_rec.setChecked(mPreferenceHelper.canAutoRecord());
            Log.d(TAG, "switch state | auto record = " + mSwitch_auto_rec.isChecked());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mEventBus.unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mActionModeBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (sIsInActionMode) {
            notifyOnActionMode();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSectionsPagerAdapter.getItem(mCurrentFragmentPosition)
                .onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_locked_records:
                startActivity(new Intent(this, RecordsActivity.class)
                        .putExtra(RecordsActivity.args.title.name(),
                                getResources().getString(R.string.title_activity_locked_records)));
                break;
            case R.id.nav_rubbished_records:
                startActivity(new Intent(this, RecordsActivity.class)
                        .putExtra(RecordsActivity.args.title.name(),
                                getResources().getString(R.string.title_activity_rubbished_records)));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_statistic:
                startActivity(new Intent(this, StatisticActivity.class));
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Adjust the layout on entering or leaving the action mode
     *
     * @param isInActionMode - whether the app is in action mode.
     */
    public void handleActionMode(boolean isInActionMode) {
        if (isInActionMode) {
            mSearchView.setVisibility(View.GONE);
            mTabs.setVisibility(View.GONE);
            mFAB_ActionMode.setImageResource(R.drawable.ic_close_white);

        } else {
            mSearchView.setVisibility(View.VISIBLE);
            mTabs.setVisibility(View.VISIBLE);
            mFAB_ActionMode.setImageResource(R.drawable.ic_delete_white);
        }

        sIsInActionMode = isInActionMode;
    }


    public FloatingActionButton getFAB_ActionMode() {
        return mFAB_ActionMode;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mSearchListener.setArguments(mSectionsPagerAdapter.getItem(tab.getPosition()).getArguments());
        mEventBus.post(new OnTabSelected(tab.getPosition()));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        mEventBus.post(new OnTabUnSelected(tab.getPosition()));
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    protected void onLoadingBegin(OnLoadingBegin event) {
        if (!mProgressBar.isShown()) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    protected void onLoadingDone(OnLoadingDone event) {
        mHandlerToWait.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
//                mSwipeContainer.setRefreshing(false);
            }
        }, 2000);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/mTabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        /**
         * Get the correct tab by position
         *
         * @param position - the desired position of the tab
         * @return - the desired tab by position
         */
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            mCurrentFragmentPosition = position;
            switch (position) {
                case 0:
                    return mMainFragment;
                case 1:
                    return mLoveFragment;
                default:
                    return null;
            }
        }

        /**
         * Get the number of available mTabs
         *
         * @return - the total number of the mTabs
         */
        @Override
        public int getCount() {
            // Show 2 total pages (TABS).
            return 2;
        }

        /**
         * Get tab's title
         *
         * @param position - the fragment (TAB)'s position
         * @return - the title of the tab
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.page_title_records);
                case 1:
                    return getString(R.string.page_title_favorite);
                default:
                    return null;
            }
        }
    }
}
