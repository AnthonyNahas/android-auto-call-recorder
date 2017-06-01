package com.anthonynahas.autocallrecorder.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
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

import com.arlib.floatingsearchview.FloatingSearchView;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.classes.Resources;
import com.anthonynahas.autocallrecorder.fragments.RecordsListFragment;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.utilities.helpers.DialogHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.MemoryCacheHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PermissionsHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;
import com.anthonynahas.autocallrecorder.utilities.support.DemoRecordSupport;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static boolean sIsInActionMode = false;

    private FloatingActionButton fabActionMode;
    private PreferenceHelper mPreferenceHelper;
    private FloatingSearchView mSearchView;
    private TabLayout mTabLayout;
    private SwitchCompat mSwitch_auto_rec;
    private int mCurrentFragmentPosition;
    private AppCompatActivity mActivity;
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

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_navigation_drawer_tabs);
        mActivity = this;

        mPreferenceHelper = new PreferenceHelper(this);

        MemoryCacheHelper.init();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);


        fabActionMode = (FloatingActionButton) findViewById(R.id.fab_go_in_action_mode);
        fabActionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyOnActionMode(sIsInActionMode = !sIsInActionMode);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
            }
        });

        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.attachNavigationDrawerToMenuButton(mDrawer);
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
                        break;
                    case R.id.action_sort:
                        Log.d(TAG, "MenuItem = sort");
                        DialogHelper.openSortDialog(mActivity, mSectionsPagerAdapter.getItem(0));
                        break;
                    case R.id.action_settings:
                        startActivity(new Intent(mActivity, SettingsActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
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
                String senderClassName = intent.getStringExtra(Resources.ACTION_MODE_SENDER);
                boolean isInActionMode = intent.getBooleanExtra(Resources.ACTION_MODE_SATE, false);
                handleActionMode(isInActionMode);
                Log.d(TAG, "on receive action mode: " + isInActionMode + " from --> " + senderClassName);
            }
        };

        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mActionModeBroadcastReceiver,
                        new IntentFilter(Resources.BROADCAST_ACTION_ON_ACTION_MODE));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // requesting required permission on run time
        PermissionsHelper permissionsHelper = new PermissionsHelper(this);
        permissionsHelper.requestAllPermissions();
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
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mActionModeBroadcastReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (sIsInActionMode) {
            notifyOnActionMode(sIsInActionMode = !sIsInActionMode);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
            mTabLayout.setVisibility(View.GONE);
            fabActionMode.setImageResource(R.drawable.ic_close_white);

        } else {
            mSearchView.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.VISIBLE);
            fabActionMode.setImageResource(R.drawable.ic_delete_white);
        }

        sIsInActionMode = isInActionMode;
    }

    /**
     * Notify all receiver that the app is going in action mode
     *
     * @param state - whether the app is in action mode (true)
     */
    private void notifyOnActionMode(boolean state) {
        Intent intent = new Intent(Resources.BROADCAST_ACTION_ON_ACTION_MODE);
        intent.putExtra(Resources.ACTION_MODE_SENDER, MainActivity.class.getSimpleName());
        intent.putExtra(Resources.ACTION_MODE_SATE, state);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);
    }

    public FloatingActionButton getFabActionMode() {
        return fabActionMode;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private RecordsListFragment mRecordsListFragment;
        private RecordsListFragment mLoveRecordsListFragment;

        private SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            mRecordsListFragment = RecordsListFragment.newInstance();

            Bundle args = new Bundle();
            String selection = RecordDbContract.RecordItem.COLUMN_IS_LOVE + " =1";
            args.putString(RecordsListFragment.BundleArgs.selection.name(), selection);
            mLoveRecordsListFragment = RecordsListFragment.newInstance(args);
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
                    mSearchView.setOnQueryChangeListener(mRecordsListFragment.getOnQueryChangeListener());
                    return mRecordsListFragment;
                case 1:
                    //mSearchView.setOnQueryChangeListener(mLoveRecordsListFragment.getOnQueryChangeListener());
                    return mLoveRecordsListFragment;
                default:
                    return null;
            }
        }

        /**
         * Get the number of available tabs
         *
         * @return - the total number of the tabs
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
