package com.anthonynahas.autocallrecorder.utilities.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.anthonynahas.autocallrecorder.providers.RecordDbContract;

/**
 * Created by A on 08.05.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 09.05.2017
 */

public class PreferenceHelper {

    private static final String TAG = PreferenceHelper.class.getSimpleName();

    public enum Key {
        AUTO_RECORD,
        AUDIO_SOURCE,
        OUTPUT_FORMAT,
        AUDIO_ENCODER,
        SORT_SELECTION,
        SORT_ARRANGE,
        DROPBOX_API_UPLOAD,
        ADD_TO_MUSIC_LIBRARY,
        TO_MOVE_IN_RECYCLE_BIN
    }

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private String mSharedPreferenceName = "com.anthonynahas.autocallrecorder.SHARED_PREFERENCE_NAME";

    public PreferenceHelper(Context context) {
        mContext = context;
        //mSharedPreferences = mContext.getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    public boolean setCanAutoRecord(boolean isAutoRecord) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Key.AUTO_RECORD.name(), isAutoRecord);
        return editor.commit();
    }

    public boolean setCanUploadOnDropBox(boolean canUpload) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Key.DROPBOX_API_UPLOAD.name(), canUpload);
        return editor.commit();
    }

    public boolean setAudioSource(int value) {
        Log.d(TAG, "on set audio source --> " + value);
        SharedPreferences sharedPref = mContext.getSharedPreferences(mSharedPreferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Key.AUDIO_SOURCE.name(), value);
        return editor.commit();
    }

    public boolean setSortSelection(String sortSelection) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Key.SORT_SELECTION.name(), sortSelection);
        return editor.commit();
    }

    public boolean setSortArrange(String sortArrange) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Key.SORT_ARRANGE.name(), sortArrange);
        return editor.commit();
    }

    public boolean canAutoRecord() {
        return mSharedPreferences.getBoolean(Key.AUTO_RECORD.name(), true);
    }

    public boolean canUploadOnDropBox() {
        return mSharedPreferences.getBoolean(Key.DROPBOX_API_UPLOAD.name(), false);
    }

    public boolean canAudioFileBeAddedToLibrary() {
        return mSharedPreferences.getBoolean(Key.ADD_TO_MUSIC_LIBRARY.name(), false);
    }

    public int getAudioSource() {
        String audioSource = mSharedPreferences.getString(Key.AUDIO_SOURCE.name(),
                String.valueOf(MediaRecorder.AudioSource.VOICE_COMMUNICATION));
        return Integer.valueOf(audioSource);
    }

    public int getOutputFormat() {
        String outputFormat = mSharedPreferences.getString(Key.OUTPUT_FORMAT.name(),
                String.valueOf(MediaRecorder.OutputFormat.MPEG_4));
        return Integer.valueOf(outputFormat);
    }

    public int getAudioEncoder() {
        String audioEncoder = mSharedPreferences.getString(Key.AUDIO_ENCODER.name(),
                String.valueOf(MediaRecorder.AudioEncoder.HE_AAC));
        return Integer.valueOf(audioEncoder);
    }

    public String getSortSelection() {
        return mSharedPreferences.getString(Key.SORT_SELECTION.name(), RecordDbContract.RecordItem.COLUMN_DATE);
    }

    public String getSortArrange() {
        return mSharedPreferences.getString(Key.SORT_ARRANGE.name(), " DESC");
    }

    public boolean toMoveInRecycleBin() {
        return mSharedPreferences.getBoolean(Key.TO_MOVE_IN_RECYCLE_BIN.name(), true);
    }
}
