package anthonynahas.com.autocallrecorder.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import anthonynahas.com.autocallrecorder.R;

/**
 * Created by A on 28.03.16.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    /**
     * inflate the preference layout on create
     * @param savedInstanceState the saved instances state bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        Log.d(TAG, "addPreferencesFromResource");
        // Make sure default values are applied.  In a real app, you would
        // want this in a shared function that is used to retrieve the
        // SharedPreferences wherever they are needed.
        //PreferenceManager.setDefaultValues(getActivity(), R.xml.advanced_preferences, false);
        addPreferencesFromResource(R.xml.preference);
    }
}
