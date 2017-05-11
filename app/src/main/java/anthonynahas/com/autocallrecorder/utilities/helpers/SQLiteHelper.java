package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.util.Log;

/**
 * Created by anahas on 08.05.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 08.05.2017
 */

public class SQLiteHelper {

    private static final String TAG = SQLiteHelper.class.getSimpleName();

    public static String convertArrayToInOperatorArguments(String[] args) {
        String result = "";

        if (args.length > 0) {
            result += " ( ";

            for (int i = 0; i < args.length; i++) {
                if (i == args.length - 1) {
                    result += args[i];
                    break;
                }
                result += args[i] + ", ";
            }
            result += " )";
        }

        result = result.isEmpty() ? " () " : result;

        Log.d(TAG, "IN = " + result);
        return result;
    }

}
