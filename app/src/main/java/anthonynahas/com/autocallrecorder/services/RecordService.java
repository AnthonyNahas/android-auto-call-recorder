package anthonynahas.com.autocallrecorder.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import anthonynahas.com.autocallrecorder.broadcasts.CallReceiver;
import anthonynahas.com.autocallrecorder.broadcasts.DoneRecReceiver;
import anthonynahas.com.autocallrecorder.utilities.helpers.FileHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.PreferenceHelper;

/**
 * Created by A on 28.03.16.
 *
 * @author Anthony Nahas
 * @version 0.5.9
 * @since 28.03.2016
 */
public class RecordService extends Service {

    private static final String TAG = RecordService.class.getSimpleName();

    private static Bundle sCallData;
    private MediaRecorder mMediaRecorder;
    private PreferenceHelper mPreferenceHelper;


    public static final String FILEPATHKEY = "filepathkey";
    public static File sRecordFile;


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
        stopRecord();
        //getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(sRecordFile.getAbsoluteFile())));
        //this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(sRecordFile.getAbsoluteFile())));
        //getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(getBaseDir().getAbsoluteFile())));
        //startService(new Intent().putExtras(sCallData));
        String[] string_rec_path = {sRecordFile.getAbsolutePath()};

        MediaScannerConnection.scanFile(getApplicationContext(), string_rec_path, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d(TAG, "scan completed");
                Intent i = new Intent();
                i.putExtras(sCallData);
                i.setAction(DoneRecReceiver.ACTION);
                sendBroadcast(i);
                Log.d(TAG, "broadcast sent");
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() - recording...");

        sCallData = intent.getExtras();
        String id_date = sCallData.getString(CallReceiver.LONGDATEKEY);
        //String number = callData.getString(CallReceiver.NUMBERKEY);
        //int isIncomingCall = callData.getInt(CallReceiver.INCOMINGCALLKEY);

        // TODO: 08.05.17 to check
        String fileSuffix = FileHelper.getAudioFileSuffix(mPreferenceHelper.getOutputFormat());
        sRecordFile = new File(FileHelper.getChildDir(Long.valueOf(id_date)), id_date + fileSuffix);
        sCallData.putString(FILEPATHKEY, sRecordFile.getAbsolutePath());

        Log.d(TAG, sRecordFile.getAbsolutePath());

        startAndSaveRecord(sRecordFile);

        return super.onStartCommand(intent, flags, startId);
    }

    private void startAndSaveRecord(File recordFile) {
        stopRecord();
        mMediaRecorder = new MediaRecorder();
        // TODO: 16.05.17 not working well
        mMediaRecorder.setAudioSource(mPreferenceHelper.getAudioSource());   //or default mic
        mMediaRecorder.setOutputFormat(mPreferenceHelper.getOutputFormat());
        mMediaRecorder.setAudioEncoder(mPreferenceHelper.getAudioEncoder()); //AMR_NB
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
            //free record ressource
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            Log.d(TAG, "media recoreder has been released");
            //getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(sRecordFile.getAbsoluteFile())));

            /*
            Intent i = new Intent();
            i.putExtras(sCallData);
            i.setAction(DoneRecReceiver.ACTION);
            sendBroadcast(i);
            */
        }
    }
}
