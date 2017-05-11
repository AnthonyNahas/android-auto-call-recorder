package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOError;

/**
 * Class helper that deals with the file system and more.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 11.05.2017
 */

public class FileHelper {

    private static final String TAG = FileHelper.class.getSimpleName();


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
