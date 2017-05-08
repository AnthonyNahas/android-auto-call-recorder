package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by A on 08.05.17.
 */

public class PreferenceHelper {

    private static final String TAG = PreferenceHelper.class.getSimpleName();

    public enum Key {
        AUDIO_SOURCE,
        OUTPUT_FORMAT,
        AUDIO_ENCODER
    }

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private String mSharedPreferenceName = "anthonynahas.com.autocallrecorder.SHARED_PREFERENCE_NAME";

    public PreferenceHelper(Context context) {
        mContext = context;
        //mSharedPreferences = mContext.getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    public boolean setAudioSource(int value) {
        Log.d(TAG, "on set audio source --> " + value);
        SharedPreferences sharedPref = mContext.getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Key.AUDIO_SOURCE.name(), value);
        return editor.commit();
    }

    public int getAudioSource() {
        String audioSource = mSharedPreferences.getString(Key.AUDIO_SOURCE.name(),
                String.valueOf(MediaRecorder.AudioSource.DEFAULT));
        return Integer.valueOf(audioSource);
    }

    public int getOutputFormat(){
        String outputFormat = mSharedPreferences.getString(Key.OUTPUT_FORMAT.name(),
                String.valueOf(MediaRecorder.OutputFormat.DEFAULT));
        return Integer.valueOf(outputFormat);
    }

    public int getAudioEncoder(){
        String audioEncoder = mSharedPreferences.getString(Key.AUDIO_ENCODER.name(),
                String.valueOf(MediaRecorder.AudioEncoder.DEFAULT));
        return Integer.valueOf(audioEncoder);
    }

}
