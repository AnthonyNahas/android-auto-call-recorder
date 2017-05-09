package anthonynahas.com.autocallrecorder.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import anthonynahas.com.autocallrecorder.broadcasts.CallReceiver;
import anthonynahas.com.autocallrecorder.broadcasts.DoneRecReceiver;
import anthonynahas.com.autocallrecorder.classes.CallRecordedFile;
import anthonynahas.com.autocallrecorder.utilities.helpers.PreferenceHelper;

/**
 * Created by A on 28.03.16.
 */
public class RecordService extends Service {

    private static final String TAG = RecordService.class.getSimpleName();
    private static final String FILENAME = "com.anthonynahas.autocallrecorder";
    private static Bundle sCallData;
    private MediaRecorder mMediaRecorder;
    private PreferenceHelper mPreferenceHelper;


    public static final String FILEPATHKEY = "filepathkey";
    public static File sRecordFile;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate()");
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
        String [] string_rec_path ={sRecordFile.getAbsolutePath()};

        MediaScannerConnection.scanFile(getApplicationContext(), string_rec_path, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d(TAG,"scan completed");
                Intent i = new Intent();
                i.putExtras(sCallData);
                i.setAction(DoneRecReceiver.ACTION);
                sendBroadcast(i);
                Log.d(TAG,"broadcast sent");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() - recording...");

        sCallData = intent.getExtras();
        String id_date = sCallData.getString(CallReceiver.LONGDATEKEY);
        //String number = callData.getString(CallReceiver.NUMBERKEY);
        //int isIncomingCall = callData.getInt(CallReceiver.INCOMINGCALLKEY);

        // TODO: 08.05.17 rename file
        sRecordFile = new File(getChildDir(Long.valueOf(id_date)),id_date + CallRecordedFile._3GP);
        sCallData.putString(FILEPATHKEY,sRecordFile.getAbsolutePath());

        /*
        ContentValues values = new ContentValues();
        values.put(RecordDbContract.RecordItem.COLUMN_ID,id_date);
        values.put(RecordDbContract.RecordItem.COLUMN_DATE,id_date);
        values.put(RecordDbContract.RecordItem.COLUMN_NUMBER,number);
        values.put(RecordDbContract.RecordItem.TABLE_NAME,recordFile.getAbsolutePath());
        values.put(RecordDbContract.RecordItem.COLUMN_INCOMING,isIncomingCall);
        mContentResolver.insert(RecordDbContract.CONTENT_URL,values);
        Log.d(TAG,"contentResolver inserted record");*/

        //String sysDate = intent.getStringExtra(CallReceiver.LONGDATEKEY);

        Log.d(TAG,sRecordFile.getAbsolutePath());

        startAndSaveRecord(sRecordFile);

        return super.onStartCommand(intent, flags, startId);
    }

    public static File getBaseDir(){
        File baseFileDir = new File(Environment.getExternalStorageDirectory(),FILENAME);
        Log.d(TAG,baseFileDir.toString());
        //make dir
        if(!baseFileDir.mkdir()){
            Log.w(TAG,"Base directory has been already given!");
        }
        return baseFileDir;
    }

    private static File getChildDir(long currentDate){
        File childFile = new File(getBaseDir().getPath(), getDate(currentDate));
        Log.d(TAG,childFile.getAbsolutePath());
        if(!childFile.mkdir()){
            Log.w(TAG,"Child directory has been already given!");
        }
        return childFile;
    }

    private void startAndSaveRecord(File recordFile){
        stopRecord();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(mPreferenceHelper.getAudioSource());   //or default mic
        mMediaRecorder.setOutputFormat(mPreferenceHelper.getOutputFormat());
        mMediaRecorder.setAudioEncoder(mPreferenceHelper.getAudioEncoder()); //AMR_NB

        try {
            if(!recordFile.createNewFile()){
                Log.w(TAG,"File name has been already given");
            }
            mMediaRecorder.setOutputFile(recordFile.getAbsolutePath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"Recording could not be starting!");
        }
    }


    private void stopRecord(){
        if(mMediaRecorder != null){
            //free record ressource
            mMediaRecorder.release();
            mMediaRecorder = null;
            Log.d(TAG,"media recoreder has been released");
            //getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(sRecordFile.getAbsoluteFile())));

            /*
            Intent i = new Intent();
            i.putExtras(sCallData);
            i.setAction(DoneRecReceiver.ACTION);
            sendBroadcast(i);
            */
        }
    }

    private static String getDate(long l){
        DateFormat dateFormat = new SimpleDateFormat("hh:mm dd-MM-yy");
        Date date = new Date(l);

        return dateFormat.format(date);
    }
}
