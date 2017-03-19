package anthonynahas.com.autocallrecorder.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import anthonynahas.com.autocallrecorder.R;
import anthonynahas.com.autocallrecorder.fragments.SettingsFragment;

/**
 * Created by A on 28.03.16.
 */
public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private SharedPreferences mSharedPreferences;

    public static final String KEY_SORT_SELECTION = "sortSelection_KEY";
    public static final String KEY_SORT_ARRANGE = "sortArrange_KEY";
    public static final String KEY_PREF_AUTO_RECORD = "autoRecord_KEY";
    public static final String KEY_PREF_AUTO_UPLOAD_ON_DROPBOX = "autoUploadOnDropBox_KEY";

    /**
     * get the setting fragment and display it
     * @param savedInstanceState the saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*setContentView(R.layout.activity_settings);
        //Toolbar Toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        //getLayoutInflater().inflate(R.layout.activity_settings, (ViewGroup)findViewById(android.R.id.content));
        Toolbar actionbar = (Toolbar)findViewById(R.id.toolbar);

        setSupportActionBar(actionbar);
        actionbar.setTitle("Settings");
        //actionbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_bac),null);
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });
        */

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }



    /**
     * On resume register the shared preference listener and reset changed-flags
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * On pause unregister the shared preference listener
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        //setResult(RESULT_OK, null);
        //finish();
    }

    /**
     * triggered when an shared preference has been changed
     * sets the changed-flags depending on what has been changed
     *
     * @param sharedPreferences the shared preferences that has been changed
     * @param key the key of the preference that has been changed within the shared preferences
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            assert root != null;
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);


            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }

        return null;
    }
}
