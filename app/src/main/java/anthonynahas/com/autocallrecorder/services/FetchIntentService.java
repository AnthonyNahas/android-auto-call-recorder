package anthonynahas.com.autocallrecorder.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOError;

import anthonynahas.com.autocallrecorder.activities.SettingsActivity;
import anthonynahas.com.autocallrecorder.broadcasts.CallReceiver;
import anthonynahas.com.autocallrecorder.providers.RecordDbContract;
import anthonynahas.com.autocallrecorder.utilities.helpers.ContactHelper;
import anthonynahas.com.autocallrecorder.utilities.helpers.DropBoxHelper;

/**
 * Created by A on 29.04.16.
 */
public class FetchIntentService extends IntentService {

    private static final String TAG = FetchIntentService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public FetchIntentService() {
        super("FetchIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"onHandleIntent()");

        Log.d(TAG,"sRecordFile = " + RecordService.sRecordFile);
        //getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(RecordService.sRecordFile)));

        Bundle callData = intent.getExtras();
        String rec_path = intent.getStringExtra(RecordService.FILEPATHKEY);
        String date = callData.getString(CallReceiver.LONGDATEKEY);
        Log.d(TAG,"date = " + date);
        String number = callData.getString(CallReceiver.NUMBERKEY);
        Log.d(TAG,"num: " + number);
        int isIncomingCall = callData.getInt(CallReceiver.INCOMINGCALLKEY);
        Log.d(TAG,"isInc = " + isIncomingCall);

        //String [] projectALL = new String[] { "*" };

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DURATION };


        String whereClause = "(" + MediaStore.Audio.Media.DISPLAY_NAME+ " == " + date + ".3gp)";
        //String selection = MediaStore.Audio.Media.DISPLAY_NAME + "=? ";
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " like ? ";
        String [] args =  {date + ".3gp"};


        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection, selection, args, null);
        String id;
        int size;
        int duration;
        String data;
        String displayName;

        int counter = 0;


        assert audioCursor != null;
        Log.d(TAG,"cursor count = " + audioCursor.getCount());
        audioCursor.moveToFirst();
        /*
        if (audioCursor.moveToFirst()) {
            while (audioCursor.moveToNext()) {
                if (audioCursor.isLast()) {
                    Log.d(TAG, "cursor is last");
                }*/
                id = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media._ID));
                data = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                size = audioCursor.getInt(audioCursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                duration = audioCursor.getInt(audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                displayName = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                // do what ever you want here
                Log.d(TAG, data + " id = " + id + " | size = " + size + " name = " + displayName);
                Log.d(TAG,"counter = " + counter++);
          //  }
        //}
        audioCursor.close();

        long contactID = ContactHelper.getContactID(this.getContentResolver(),number);


        ContentValues values = new ContentValues();
        values.put(RecordDbContract.RecordItem.COLUMN_ID,id);
        values.put(RecordDbContract.RecordItem.COLUMN_DATE,date);
        values.put(RecordDbContract.RecordItem.COLUMN_NUMBER,number);
        values.put(RecordDbContract.RecordItem.COLUMN_INCOMING,isIncomingCall);
        values.put(RecordDbContract.RecordItem.COLUMN_SIZE,size);
        values.put(RecordDbContract.RecordItem.COLUMN_DURATION,duration);
        if(contactID != -1){
            values.put(RecordDbContract.RecordItem.COLUMN_CONTACTID,contactID);
        }
        getApplicationContext().getContentResolver().insert(RecordDbContract.CONTENT_URL,values);
        Log.d(TAG,"contentResolver inserted record");

        Log.d(TAG,"---------------------------------------------------------------");

        //logRecContentProvider();

        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsActivity.KEY_PREF_AUTO_UPLOAD_ON_DROPBOX, false)){
            Log.d(TAG,"onUpload()");
            uploadAudioFile(data,displayName);

        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(TAG,"onStart()");
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy() fetching..");
        super.onDestroy();
    }

    private void getAllChildDirectories(File parentDir){

        try{
            File [] allFiles = parentDir.listFiles();
            logFiles(allFiles);
        }
        catch (IOError e){
            Log.e(TAG,"error " + e);
        }
    }

    private void logFiles(File[] files){
        for(File f:files){
            Log.d(TAG,f.getName() + " isDir = " + f.isDirectory());
        }
    }

    private void logRecContentProvider(){
        Cursor c = getApplicationContext().getContentResolver().query(RecordDbContract.CONTENT_URL, new String[] { "*" }, null, null, null);

        assert c != null;
        if (c.moveToFirst()) {
            do {
                String id = c.getString(c.getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID));
                String number = c.getString(c.getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER));
                long date = c.getLong(c.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE));
                int size = c.getInt(c.getColumnIndex(RecordDbContract.RecordItem.COLUMN_SIZE));
                int duration = c.getInt(c.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION));
                int incoming = c.getInt(c.getColumnIndex(RecordDbContract.RecordItem.COLUMN_INCOMING));
                // do what ever you want here
                Log.d(TAG," id = " + id + " | number = " + number + " | date = "+ date +" | size = " + size + " | duration = " + duration + " | incoming = " +incoming );
            } while (c.moveToNext());
        }
        c.close();
    }

    private boolean uploadAudioFile(String filePath, String fileName){
        DropBoxHelper dropBoxHelper = new DropBoxHelper(this);
        File tmpFile = new File(filePath);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(tmpFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            dropBoxHelper.getApi().putFileOverwrite(DropBoxHelper.getDropboxFileDir() + fileName, fis, tmpFile.length(), null);
            //showToastFromService("File has been uploaded!");
            return true;
        } catch (DropboxUnlinkedException e) {
            Log.e("DbExampleLog", "User has unlinked.");
        } catch (DropboxException e) {
            Log.e("DbExampleLog", "Something went wrong while uploading.");
        }
        //showToastFromService("Error occured while processing the upload request");
        return false;
    }

    private void showToastFromService(final String msg){
        Handler h = new Handler(getApplicationContext().getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            }
        });
    }
}
