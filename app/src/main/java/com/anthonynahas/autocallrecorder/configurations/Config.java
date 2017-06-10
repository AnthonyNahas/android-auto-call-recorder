package com.anthonynahas.autocallrecorder.configurations;

import android.os.Bundle;

import com.anthonynahas.autocallrecorder.providers.RecordDbContract;

/**
 * Created by A on 10.06.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @see 10.06.17
 */

public class Config {

    public enum args {
        title,
        projection,
        selection,
        selectionArguments,
        limit,
        offset
    }

    public static Bundle RECORDSFRAGMENT = getBundleForRecordsFragment();

    private static Bundle getBundleForRecordsFragment() {
        Bundle args = new Bundle();
        String[] projection = {"*"};
        String selection = RecordDbContract.RecordItem.COLUMN_IS_LOVE + " = 1";
        int limit = 15; //default
        int offset = 0; //default
        args.putStringArray(Config.args.projection.name(),projection);
        args.putString(Config.args.selection.name(), selection);
        args.putInt(Config.args.limit.name(), limit);
        args.putInt(Config.args.offset.name(), offset);

        return args;
    }

}
