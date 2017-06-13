package com.anthonynahas.autocallrecorder.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.anthonynahas.autocallrecorder.models.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that deal with the content provider in otder to simplify the work within an
 * activity or fragment
 *
 * @author Anthony Nahas
 * @version 1.2
 * @since 17.04.17
 */

public class RecordDbHelper {

    private static final String TAG = RecordDbHelper.class.getSimpleName();

    public static RecordDbHelper newInstance() {
        return new RecordDbHelper();
    }


    /**
     * Update a boolean column of the db asynchronously
     *
     * @param context        - the used context
     * @param columnToUpdate - the column of the db to update
     * @param id-            the id of the record
     * @param value          - the value to put to the target column to update it
     */
    public void updateBooleanColumn(Context context, String columnToUpdate, int id, int value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnToUpdate, value);
        Uri uri = ContentUris.withAppendedId(RecordDbContract.CONTENT_URL, (long) id);
        String selection = RecordDbContract.RecordItem.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        RecordsQueryHandler.getInstance(context.getContentResolver())
                .startUpdate(0, null, uri, contentValues, selection, selectionArgs);
    }


    public static void updateIsLockedColumn(Context context, int id, int isLocked) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecordDbContract.RecordItem.COLUMN_IS_LOCKED, isLocked);
        Uri uri = ContentUris.withAppendedId(RecordDbContract.CONTENT_URL, (long) id);
        String selection = RecordDbContract.RecordItem.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        RecordsQueryHandler.getInstance(context.getContentResolver())
                .startUpdate(RecordsQueryHandler.update.UPDATE_IS_LOCKED.ordinal(), null, uri,
                        contentValues, selection, selectionArgs);
    }


    /**
     * Convert the receiver cursor from the db to a list of recrods object and return it.
     *
     * @param cursor - the cursor received from db
     * @return - the converted cursor into list of records
     */
    public static List<Record> convertCursorToRecordList(Cursor cursor) {

        List<Record> recordsList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                int _id = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID));
                String number = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER));
                long contact_id = cursor.getLong(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_CONTACT_ID));
                long date = cursor.getLong(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE));
                int size = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_SIZE));
                int duration = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION));
                int isIncoming = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_INCOMING));
                int isLove = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_LOVE));

                Record record = new Record(_id, number, contact_id, date, size, duration, isIncoming == 1, isLove == 1);
                recordsList.add(record);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return recordsList;

    }


    public static List<Record> convertCursorToContactRecordsList(Cursor cursor) {

        List<Record> contactRecordList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {

                String number = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_NUMBER));
                long contact_ID = cursor.getLong(cursor.getColumnIndexOrThrow(RecordDbContract.RecordItem.COLUMN_CONTACT_ID));
                long date = cursor.getLong(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE));
                int size = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_SIZE));
                int duration = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DURATION));
                int isIncoming = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_INCOMING));
                int isLove = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_LOVE));
                int totalCalls = cursor.getInt(cursor.getColumnIndexOrThrow(RecordDbContract.Extended.COLUMN_TOTAL_CALLS));
                int totalIncomingCalls = cursor.getInt(cursor.getColumnIndexOrThrow(RecordDbContract.Extended.COLUMN_TOTAL_INCOMING_CALLS));

                Record record = new Record();
                record.setNumber(number);
                record.setContactID(contact_ID);
                record.setDate(date);
                record.setSize(size);
                record.setDuration(duration);
                record.setIncoming(isIncoming);
                record.setLove(isLove);
                record.setRank(cursor.getPosition() + 1);
                record.setTotalIncomingCalls(totalIncomingCalls);
                record.setTotalOutgoingCall(totalCalls - totalIncomingCalls);

                contactRecordList.add(record);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return contactRecordList;

    }

}
