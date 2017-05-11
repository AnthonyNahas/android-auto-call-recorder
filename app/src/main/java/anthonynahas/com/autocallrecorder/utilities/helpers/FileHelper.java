package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.media.MediaRecorder;
import android.os.Environment;
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


    public static File getChildDir(long currentDate) {
        File childFile = new File(getBaseDir().getPath(), getDate(currentDate));
        Log.d(TAG, childFile.getAbsolutePath());
        if (!childFile.mkdir()) {
            Log.w(TAG, "Child directory has been already given!");
        }
        return childFile;
    }

    public static File getBaseDir() {
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
