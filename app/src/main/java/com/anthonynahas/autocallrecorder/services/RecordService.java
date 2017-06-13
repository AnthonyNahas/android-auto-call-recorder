package com.anthonynahas.autocallrecorder.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.anthonynahas.autocallrecorder.broadcasts.DoneRecReceiver;
import com.anthonynahas.autocallrecorder.classes.Res;
import com.anthonynahas.autocallrecorder.models.Record;
import com.anthonynahas.autocallrecorder.utilities.helpers.FileHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.PreferenceHelper;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

/**
 * Created by A on 28.03.16.
 *
 * @author Anthony Nahas
 * @version 0.5.9
 * @since 28.03.2016
 */
public class RecordService extends Service {

    private static final String TAG = RecordService.class.getSimpleName();

    @Inject
    FileHelper mFileHelper;

    public static File sRecordFile;

    private Record mRecord;
    private Handler mRecordHandler;
    private MediaRecorder mMediaRecorder;
    private PreferenceHelper mPreferenceHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");
        mPreferenceHelper = new PreferenceHelper(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() - stop recording...");

        mRecordHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopRecord();

                String[] string_rec_path = {sRecordFile.getAbsolutePath()};

                if (mPreferenceHelper.canAudioFileBeAddedToLibrary()) {
                    MediaScannerConnection.scanFile(getApplicationContext(), string_rec_path, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d(TAG, "scan completed");
                            sendBroadcast(new Intent(DoneRecReceiver.ACTION)
                                    .putExtra(Res.REC_PARC_KEY, (Parcelable) mRecord));
                        }
                    });
                } else {
                    sendBroadcast(new Intent(DoneRecReceiver.ACTION)
                            .putExtra(Res.REC_PARC_KEY, (Parcelable) mRecord));
                }
            }
        }, 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() - recording...");

        mRecord = intent.getParcelableExtra(Res.REC_PARC_KEY);

        // TODO: 08.05.17 to check
        String fileSuffix = mFileHelper.getAudioFileSuffix(mPreferenceHelper.getOutputFormat());
        sRecordFile = new File(mFileHelper.getChildDir(mRecord.getDate()), mRecord.getDate() + fileSuffix);
        mRecord.setPath(sRecordFile.getAbsolutePath());

        Log.d(TAG, sRecordFile.getAbsolutePath());
        startAndSaveRecord(sRecordFile);

        mRecordHandler = new Handler(getMainLooper());
        mRecordHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "handler run ...");

            }
        }, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    private void startAndSaveRecord(File recordFile) {
        stopRecord();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        // TODO: 16.05.17 not working well
        mMediaRecorder.setAudioSource(mPreferenceHelper.getAudioSource());   //or default voice_communication
        mMediaRecorder.setOutputFormat(mPreferenceHelper.getOutputFormat()); //MP4
        mMediaRecorder.setAudioEncoder(mPreferenceHelper.getAudioEncoder()); //AAC
        mMediaRecorder.getMaxAmplitude();
        try {
            if (!recordFile.createNewFile()) {
                Log.i(TAG, "File name has been already given");
            }
            mMediaRecorder.setOutputFile(recordFile.getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            Log.e(TAG, "Error: Recording could not be starting!", e);
        }
    }


    private void stopRecord() {
        if (mMediaRecorder != null) {
            //free record resource
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            Log.d(TAG, "media recorder has been released");

        }
    }
}
