package com.anthonynahas.autocallrecorder.utilities.helpers;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by anahas on 08.05.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 08.05.2017
 */
@Singleton
public class SQLiteHelper {

    private static final String TAG = SQLiteHelper.class.getSimpleName();

    @Inject
    public SQLiteHelper() {
    }

    public String convertArrayToInOperatorArguments(String[] args) {
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
