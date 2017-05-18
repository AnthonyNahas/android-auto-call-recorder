package anthonynahas.com.autocallrecorder.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.fragments.MainFragment;
import anthonynahas.com.autocallrecorder.utilities.helpers.PreferenceHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.UploadFile;

public class MainOldActivity extends AppCompatActivity {

    private static final String TAG = MainOldActivity.class.getSimpleName();

    public static int REQUEST_CODE_SETTINGS = 12;

    public final static String DROPBOX_FILE_DIR = "/DropboxDemo/";
    public final static String DROPBOX_NAME = "dropbox_prefs";
    private final static AccessType ACCESS_TYPE = AccessType.DROPBOX;
    final static public String DROPBOX_APP_KEY = "32fcvkt4b7ym4sv";
    final static public String DROPBOX_APP_SECRET = "dj1ac0hwabq0f71";
    static final int REQUEST_LINK_TO_DBX = 0;


    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////


    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    public static DropboxAPI<AndroidAuthSession> mApi;
    private boolean mLoggedIn;

    public Context context;
    private Toolbar mToolbar;


    private SharedPreferences mSharedPreferences;

    private PreferenceHelper mPreferenceHelper;

    private DrawerLayout mDrawerLayout;
    private NavigationView nvDrawer;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private SwitchCompat mSwitch_auto_rec;
    private SwitchCompat mSwitch_auto_upload_on_DropBox;

    //Memory Cache
    private static LruCache<String, Bitmap> mMemoryCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main_plus);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        context = MainOldActivity.this;
        // Set a Toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mPreferenceHelper = new PreferenceHelper(this);

        /************** Memory Cache ***************/
        final int maxMemorySize = (int) Runtime.getRuntime().maxMemory() / 1024;
        final int cacheSize = maxMemorySize / 10;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };

        //----------*** Navigation Menu ***---------------------------------------------------------


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarDrawerToggle = setupDrawerToggle();
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        Menu menu = nvDrawer.getMenu();
        MenuItem menuItem_switch_auto_rec = menu.findItem(R.id.nav_switch_auto_rec);
        View actionView = MenuItemCompat.getActionView(menuItem_switch_auto_rec);
        mSwitch_auto_rec = (SwitchCompat) actionView;
        MenuItem menuItem_switch_auto_upload_on_DropBox = menu.findItem(R.id.nav_switch_auto_upload_on_dropbox);
        View actionView2 = MenuItemCompat.getActionView(menuItem_switch_auto_upload_on_DropBox);
        mSwitch_auto_upload_on_DropBox = (SwitchCompat) actionView2;

        /*      ######## DROP BOX CORE API  ######## ------------------------------------------   */

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<>(session);

        // Display the proper UI state if logged in or not
        setLoggedIn(mApi.getSession().isLinked());


        /*      ######## DROP BOX CORE API  ########    */


        if (mSwitch_auto_rec != null) {
            mSwitch_auto_rec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d(TAG, "autorecord = " + mSwitch_auto_rec.isChecked());
                    mPreferenceHelper.setCanAutoRecord(isChecked);
                }
            });
        }

        if (mSwitch_auto_upload_on_DropBox != null) {
            mSwitch_auto_upload_on_DropBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mPreferenceHelper.setCanUploadOnDropBox(isChecked);
                    if (isChecked) {
                        authenticate_dropboxAccount();
                    }
                    Log.d(TAG, "autoUploadOnDropBox = "
                            + mSwitch_auto_upload_on_DropBox.isChecked());
                }
            });
        }

        loadDefaultFragment();


    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "onPostCreate()");
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_opened, R.string.drawer_closed);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        //Checking if the item is in checked state or not, if not make it in checked state
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_fragment_home:
                //fragment = new MainFragment();
                // Highlight the selected item has been done by NavigationView
                menuItem.setChecked(true);
                // Close the navigation drawer
                mDrawerLayout.closeDrawers();
                break;
            case R.id.navigation_fragment_settings:
                //fragment = new SettingsFragment();
                menuItem.setChecked(true);
                startActivityForResult(new Intent(MainOldActivity.this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
                //menuItem.setChecked(true);
                // Close the navigation drawer
                mDrawerLayout.closeDrawers();

            default:
                break;
        }

        //load fragment
        if (fragment != null) {
            getFragmentManager().beginTransaction().
                    replace(R.id.fragmentToShow, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the navigation drawer
            mDrawerLayout.closeDrawers();
        }
    }

    private void loadDefaultFragment() {
        getFragmentManager().beginTransaction().
                replace(R.id.fragmentToShow, new MainFragment()).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        if (mDrawerLayout != null) {
            mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
            Log.d(TAG, "addingListener");
        }

        nvDrawer.getMenu().findItem(R.id.navigation_fragment_home).setChecked(true);

        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.syncState();
        }

        if (mSwitch_auto_rec != null) {
            mSwitch_auto_rec.setChecked(mPreferenceHelper.canAutoRecord());
            Log.d(TAG, "switch state | auto record = " + mSwitch_auto_rec.isChecked());
        }
        if (mSwitch_auto_upload_on_DropBox != null) {
            mSwitch_auto_upload_on_DropBox.setChecked(mPreferenceHelper.canUploadOnDropBox());
            Log.d(TAG, "switch state = | auto upload on dropbox " + mSwitch_auto_upload_on_DropBox.isChecked());
        }

        //loadAuth(mDropboxAPI.getSession());

        /*      ######## DROP BOX CORE API      */

        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                storeAuth(session);
                setLoggedIn(true);
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }

        /*      ######## DROP BOX CORE API      */

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDrawerLayout.removeDrawerListener(mActionBarDrawerToggle);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        //searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconified(true);
                searchView.clearFocus();
                (menu.findItem(R.id.action_search)).collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchRecord) {
                Log.d(TAG, "search: " + searchRecord);
                sendBroadcast(searchRecord);
                return false;
            }
        });
        return true;
    }

    /**
     * Perform an action when a menu item is selected
     *
     * @param item the selected item
     * @return whether the action has been performed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle action bar item clicks here. The action bar will automatically
        //handle clicks on the home/up button
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                //mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                Intent intent2 = new Intent(this, SettingsActivity.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSearchRequested() {
        Log.d(TAG, "onSearchRequested()");
        return super.onSearchRequested();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission(final String permission, final int requestCode) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Call phone permission is necessary to make records!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: //demo
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadDefaultFragment();
                } else {
                    Toast.makeText(context, "Call phone permission is required", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1: //demo
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mSwitch_auto_rec.setChecked(true);
                } else {
                    Toast.makeText(context, "Microphone permission is required", Toast.LENGTH_SHORT).show();
                    if (mSwitch_auto_rec.isChecked()) {
                        mSwitch_auto_rec.setChecked(false);
                    }
                }
                break;
        }
    }

    public void upload() {
        UploadFile uploadFile = new UploadFile(this, mApi, DROPBOX_FILE_DIR);
        uploadFile.execute();
    }

    /*############################# DROPBOX ######################################################*/

    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeAuth(AndroidAuthSession session) {
        // Store the OAuth 2 access token, if there is one.
        String oauth2AccessToken = session.getOAuth2AccessToken();
        if (oauth2AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, "oauth2:");
            edit.putString(ACCESS_SECRET_NAME, oauth2AccessToken);
            edit.apply();
            return;
        }
        // Store the OAuth 1 access token, if there is one.  This is only necessary if
        // you're still using OAuth 1.
        AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
        if (oauth1AccessToken != null) {
            SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(ACCESS_KEY_NAME, oauth1AccessToken.key);
            edit.putString(ACCESS_SECRET_NAME, oauth1AccessToken.secret);
            edit.apply();
        }
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.apply();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(DROPBOX_APP_KEY, DROPBOX_APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    public void authenticate_dropboxAccount() {

        String[] sAuthenticatedUid = {"dummy"};

        if (mLoggedIn) {
            //logOut();
        } else {
            // Start the remote authentication
            if (USE_OAUTH1) {
                mApi.getSession().startAuthentication(MainOldActivity.this);
            } else {
                mApi.getSession().startOAuth2Authentication(MainOldActivity.this, "", sAuthenticatedUid);
            }
        }
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
    }
    /*############################# DROPBOX ######################################################*/

    /**************
     * Memory Cache
     ***************/
    public static Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    public static void setBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private void sendBroadcast(String searchItem) {
        Intent intent = new Intent();
        intent.setAction("SEARCH_ACTION");
        intent.putExtra("SEARCH_KEY", searchItem);
        sendBroadcast(intent);
    }
}

