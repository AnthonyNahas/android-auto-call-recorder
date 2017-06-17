package com.anthonynahas.autocallrecorder.providers.cursors;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by anahas on 07.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 07.06.17
 */
@Singleton
public class CursorLogger {

    private static final String TAG = CursorLogger.class.getSimpleName();

    @Inject
    public CursorLogger() {
    }

    public String log(@NonNull Cursor cursor) {
        if (cursor.getCount() == 0 || cursor.isClosed() && !cursor.moveToFirst()) {
            String log = "cursor is either empty or closed";
            printAndLog(log);
            return log;
        }

        List<String> columnNames = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
        String tab = " | ";
        String breakLine = "\n";
        String table = "";
        String log = "";
        for (String column : columnNames) {
            log += column + tab;
        }
        printAndLog(log);
        table += log + breakLine;

        if (cursor.moveToFirst()) {
            do {
                String row = "";

                for (String column : columnNames) {
                    String columnData = cursor.getString(cursor.getColumnIndex(column));
                    row += columnData + tab;
                }
                table += row + breakLine;

            } while (cursor.moveToNext());
        }

        cursor.moveToFirst();

        printAndLog(table);

        return table;
    }

    private void printAndLog(Object log) {
        System.out.println(log);
        Log.d(TAG, log.toString());
    }

}