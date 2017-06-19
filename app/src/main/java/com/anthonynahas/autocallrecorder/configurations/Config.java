package com.anthonynahas.autocallrecorder.configurations;

import android.os.Bundle;

import com.anthonynahas.autocallrecorder.providers.RecordDbContract;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by A on 10.06.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @see 10.06.17
 */
@Singleton
public class Config {

    public enum args {
        title,
        projection,
        selection,
        selectionArguments,
        limit,
        offset
    }

    @Inject
    public Config() {
    }

    public Bundle record_fragment_main = getBundleForRecordsFragment_MAIN();
    public Bundle record_fragment_love = getBundleForRecordsFragment_LOVE();

    private Bundle getBundleForRecordsFragment_MAIN() {
        Bundle args = new Bundle();
        String[] projection = {"*"};
        String selection = null;
        int limit = 15; //default
        int offset = 0; //default
        args.putStringArray(Config.args.projection.name(), projection);
        args.putString(Config.args.selection.name(), selection);
        args.putInt(Config.args.limit.name(), limit);
        args.putInt(Config.args.offset.name(), offset);

        return args;
    }

    private Bundle getBundleForRecordsFragment_LOVE() {
        Bundle args = new Bundle();
        String[] projection = {"*"};
        String selection = RecordDbContract.RecordItem.COLUMN_IS_LOVE + " = 1";
        int limit = 15; //default
        int offset = 0; //default
        args.putStringArray(Config.args.projection.name(), projection);
        args.putString(Config.args.selection.name(), selection);
        args.putInt(Config.args.limit.name(), limit);
        args.putInt(Config.args.offset.name(), offset);

        return args;
    }

}
