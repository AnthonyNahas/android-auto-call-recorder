package com.anthonynahas.autocallrecorder.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.configurations.Constant;
import com.anthonynahas.autocallrecorder.configurations.Config;
import com.anthonynahas.autocallrecorder.fragments.RecordsFragment;
import com.anthonynahas.autocallrecorder.fragments.RecordsListFragment;
import com.anthonynahas.autocallrecorder.fragments.dialogs.InputDialog;
import com.anthonynahas.autocallrecorder.utilities.helpers.DialogHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PermissionsHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;
import com.anthonynahas.ui_animator.sample.SampleMainActivity;
import com.arlib.floatingsearchview.FloatingSearchView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Module;
import dagger.Provides;
import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static boolean sIsInActionMode = false;

    private Activity mAppCompatActivity;


    @Inject
    InputDialog mInputDialog;

    @Inject
    PreferenceHelper mPreferenceHelper;

    @Inject
    Constant mConstant;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.tabs)
    TabLayout tabs;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.floating_search_view)
    FloatingSearchView searchView;

    @BindView(R.id.fab_go_in_action_mode)
    FloatingActionButton fabActionMode;

    @StringRes
    int mNavDrawerOpen = R.string.navigation_drawer_open;

    @StringRes
    int mNavDrawerClose = R.string.navigation_drawer_close;

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

        tabs.setupWithViewPager(mViewPager);

        fabActionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyOnActionMode(sIsInActionMode = !sIsInActionMode);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //      .setAction("Action", null).show();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, mNavDrawerOpen, mNavDrawerClose);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final AppCompatActivity appCompatActivity = this;

        searchView.attachNavigationDrawerToMenuButton(drawer);
        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
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
                        DialogHelper.openSortDialog(appCompatActivity, mSectionsPagerAdapter.getItem(0));
                        break;
                    case R.id.action_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });

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
            searchView.setVisibility(View.GONE);
            tabs.setVisibility(View.GONE);
            fabActionMode.setImageResource(R.drawable.ic_close_white);

        } else {
            searchView.setVisibility(View.VISIBLE);
            tabs.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(mConstant.BROADCAST_ACTION_ON_ACTION_MODE);
        intent.putExtra(mConstant.ACTION_MODE_SENDER, MainActivity.class.getSimpleName());
        intent.putExtra(mConstant.ACTION_MODE_SATE, state);
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
        private RecordsFragment mLoveRecordsFragment;

        private SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            mRecordsListFragment = RecordsListFragment.newInstance();

            mLoveRecordsFragment = RecordsFragment.newInstance(Config.RECORDSFRAGMENT);
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
                    searchView.setOnQueryChangeListener(mRecordsListFragment.getOnQueryChangeListener());
                    return mRecordsListFragment;
                case 1:
                    //searchView.setOnQueryChangeListener(mLoveRecordsFragment.getOnQueryChangeListener());
                    return mLoveRecordsFragment;
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
