package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOError;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class helper that deals with the file system and more.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 11.05.2017
 */

public class FileHelper {

    private static final String TAG = FileHelper.class.getSimpleName();

    private static final String FILENAME = "com.anthonynahas.autocallrecorder";

    /**
     * Share a record audio file
     *
     * @param context - the used context
     * @param recordID - the id of the record file that will be shared
     */
    public static void share(Context context, String recordID) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM,
                ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        Long.valueOf(recordID)));
        context.startActivity(Intent.createChooser(share, "Share Sound File"));
    }

    public static File getChildDir(long currentDate) {
        File childFile = new File(getBaseDir().getPath(), getDate(currentDate));
        Log.d(TAG, childFile.getAbsolutePath());
        if (!childFile.mkdir()) {
            Log.w(TAG, "Child directory has been already given!");
        }
        return childFile;
    }

    private static File getBaseDir() {
        File baseFileDir = new File(Environment.getExternalStorageDirectory(), FILENAME);
        Log.d(TAG, baseFileDir.toString());
        //make dir
        if (!baseFileDir.mkdir()) {
            Log.w(TAG, "Base directory has been already given!");
        }
        return baseFileDir;
    }

    private static String getDate(long l) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Date date = new Date(l);

        return dateFormat.format(date);
    }


    public static String getAudioFileSuffix(int audioOutPutFormat) {

        switch (audioOutPutFormat) {

            case MediaRecorder.OutputFormat.AMR_NB:
            case MediaRecorder.OutputFormat.AMR_WB:

                return ".amr";

            case MediaRecorder.OutputFormat.AAC_ADTS:
                return ".aac";

            case MediaRecorder.OutputFormat.MPEG_4:

                return ".mp4";

            case MediaRecorder.OutputFormat.THREE_GPP: //deprecated

                return ".3gp";

            default:
                return "";

        }

    }


    private void getAllChildDirectories(File parentDir) {

        try {
            File[] allFiles = parentDir.listFiles();
            logFiles(allFiles);
        } catch (IOError e) {
            Log.e(TAG, "error " + e);
        }
    }

    private void logFiles(File[] files) {
        for (File f : files) {
            Log.d(TAG, f.getName() + " isDir = " + f.isDirectory());
        }
    }


}
