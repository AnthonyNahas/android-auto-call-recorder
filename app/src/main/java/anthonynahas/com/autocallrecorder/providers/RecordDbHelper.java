package anthonynahas.com.autocallrecorder.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import anthonynahas.com.autocallrecorder.classes.ContactRecord;
import anthonynahas.com.autocallrecorder.classes.Record;

/**
 * Helper class that deal with the content provider in otder to simplify the work within an
 * activity or fragment
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 17.04.17
 */

public class RecordDbHelper {

    private static final String TAG = RecordDbHelper.class.getSimpleName();

    /**
     * Update the column isLove using the content resolver
     *
     * @param context - the used context
     * @param id      - the id of the record
     * @param isLove  - whether the record isLove (favorite)
     */
    public static void updateIsLoveColumn(Context context, String id, int isLove) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecordDbContract.RecordItem.COLUMN_IS_LOVE, isLove);
        Uri uri = ContentUris.withAppendedId(RecordDbContract.CONTENT_URL, Long.valueOf(id));
        Log.d(TAG, "onUpdate --> uri = " + uri);
        String selection = RecordDbContract.RecordItem.COLUMN_ID + "=?";
        String[] selectionArgs = {id};
        RecordsQueryHandler.getInstance(context.getContentResolver())
                .startUpdate(RecordsQueryHandler.UPDATE_IS_LOVE, null, uri, contentValues, selection, selectionArgs);
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
                String _id = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID));
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
                int totalCalls = cursor.getInt(cursor.getColumnIndexOrThrow(RecordDbContract.Extended.COLUMN_TOTAL_CALLS));

                Record record = new Record();
                record.setNumber(number);
                record.setRank(totalCalls);

                contactRecordList.add(record);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return contactRecordList;

    }

}
