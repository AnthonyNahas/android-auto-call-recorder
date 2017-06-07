package com.anthonynahas.autocallrecorder.utilities.support;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.anthonynahas.autocallrecorder.classes.Record;
import com.anthonynahas.autocallrecorder.classes.Resources;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.providers.RecordsQueryHandler;
import com.anthonynahas.autocallrecorder.providers.cursors.CursorLogger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

/**
 * This class is responsible to mock and generate demo records for test
 * and debug purposes.
 *
 * @author Anthony Nahas
 * @version 0.5.0
 * @since 25.04.2017
 */

public class DemoRecordSupport {

    private static final String TAG = DemoRecordSupport.class.getSimpleName();

    //todo generatePics with lru cache

    public static DemoRecordSupport newInstance() {
        return new DemoRecordSupport();
    }

    /**
     * Create a demo record and save it in the db using the content resolver
     *
     * @param context - the used context to add the demo record
     */
    public void createDummyRecord(Context context) {
        ContentValues values = new ContentValues();
        values.put(RecordDbContract.RecordItem.COLUMN_ID, String.valueOf(generateNumber(10000, 5000)));
        values.put(RecordDbContract.RecordItem.COLUMN_DATE, generateDate());
        values.put(RecordDbContract.RecordItem.COLUMN_NUMBER, generatePhoneNumber());
        values.put(RecordDbContract.RecordItem.COLUMN_IS_INCOMING, generateNumber(1, 0));
        values.put(RecordDbContract.RecordItem.COLUMN_IS_LOVE, generateNumber(1, 0));
        values.put(RecordDbContract.RecordItem.COLUMN_SIZE, generateNumber(100, 1));
        values.put(RecordDbContract.RecordItem.COLUMN_DURATION, generateNumber(800, 400));
        RecordsQueryHandler.getInstance(context.getContentResolver())
                .startInsert(RecordsQueryHandler.INSERT_DEMO, null, RecordDbContract.CONTENT_URL, values);

        //context.getContentResolver().insert(RecordDbContract.CONTENT_URL, values);
        Log.d(TAG, "contentResolver inserted dummy record");
    }

    /**
     * create and insert db record using a real contact information.
     *
     * @param context   - the used context
     * @param contactID - the target contact id. If id = -1 then get a random one
     */
    public void createDemoRecord(final Context context, long contactID) {

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        // TODO: 17.05.2017 @contact_ID
        String[] projection =
                {
                        ContactsContract.PhoneLookup.CONTACT_ID,
//                        ContactsContract.CommonDataKinds.Phone._ID, //the same as phonelookup_ID
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                };

        String selection = contactID != 0 ? ContactsContract.PhoneLookup.CONTACT_ID + " = ?" : null;
        String[] selectionArguments = contactID != 0
                ? new String[]{String.valueOf(contactID)} : null;

        String orderBy = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(context.getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

                if (cursor.getCount() == 0) {
//                    createDummyRecord(context);
                    return;
                }

                CursorLogger.newInstance().log(cursor);

                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                } else {
                    int random = generateNumber(cursor.getCount() - 1, 0);
                    cursor.moveToPosition(random);
                }

                long contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup.CONTACT_ID));
                String contact_number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                ContentValues values = new ContentValues();
                values.put(RecordDbContract.RecordItem.COLUMN_ID, String.valueOf(generateNumber(10000, 5000)));
                values.put(RecordDbContract.RecordItem.COLUMN_PATH, Resources.DEMO_PATH);
                values.put(RecordDbContract.RecordItem.COLUMN_DATE, generateDate());
                values.put(RecordDbContract.RecordItem.COLUMN_NUMBER, contact_number);
                values.put(RecordDbContract.RecordItem.COLUMN_CONTACT_ID, contact_id);
                values.put(RecordDbContract.RecordItem.COLUMN_SIZE, generateNumber(100, 1));
                values.put(RecordDbContract.RecordItem.COLUMN_DURATION, generateNumber(600, 0));
                values.put(RecordDbContract.RecordItem.COLUMN_IS_INCOMING, generateNumber(1, 0));
                values.put(RecordDbContract.RecordItem.COLUMN_IS_LOVE, generateNumber(1, 0));

                RecordsQueryHandler.getInstance(context.getContentResolver())
                        .startInsert(RecordsQueryHandler.INSERT_DEMO, null, RecordDbContract.CONTENT_URL, values);
            }
        };

        // retrieve all contacts
        asyncQueryHandler.startQuery(0, null, uri, projection, selection, selectionArguments, orderBy);

    }

    /**
     * generate a date to to mock the demo record object
     * --> the date that the demo call has been recorded
     *
     * @return - the generated date as long millis
     */
    private long generateDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(Calendar.YEAR, generateNumber(calendar.get(Calendar.YEAR), 2015));
        calendar.set(Calendar.MONTH, generateNumber(12, 1));
        calendar.set(Calendar.DAY_OF_MONTH, generateNumber(29, 1));
        calendar.set(Calendar.HOUR_OF_DAY, generateNumber(24, 0));
        calendar.set(Calendar.MINUTE, generateNumber(59, 0));
        calendar.set(Calendar.SECOND, generateNumber(59, 9));
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * @param max
     * @param min
     * @return - the generated number
     */
    private int generateNumber(int max, int min) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    /**
     * Generate a phone number in order to mock the phone number of a demo record
     *
     * @return - the generated phone number
     */
    private String generatePhoneNumber() {
        int num1, num2, num3; //3 numbers in area code
        int set2, set3; //sequence 2 and 3 of the phone number

        Random generator = new Random();

        //Area code number; Will not print 8 or 9
        num1 = generator.nextInt(7) + 1; //add 1 so there is no 0 to begin
        num2 = generator.nextInt(8); //randomize to 8 becuase 0 counts as a number in the generator
        num3 = generator.nextInt(8);

        // Sequence two of phone number
        // the plus 100 is so there will always be a 3 digit number
        // randomize to 643 because 0 starts the first placement so if i randomized up to 642 it would only go up yo 641 plus 100
        // and i used 643 so when it adds 100 it will not succeed 742
        set2 = generator.nextInt(643) + 100;

        //Sequence 3 of numebr
        // add 1000 so there will always be 4 numbers
        //8999 so it wont succed 9999 when the 1000 is added
        set3 = generator.nextInt(8999) + 1000;

        return "(+" + num1 + "" + num2 + "" + num3 + ")" + "-" + set2 + "-" + set3;
    }


    /**
     * Generate a list of record in order to set it in the statistic adpater as demo
     *
     * @param howManyRecords - the maximum number of records object to generate and add to the list
     * @return - the generated array list of records
     */
    public List<Record> generateRecordsList(int howManyRecords) {

        List<Record> recordsList = new ArrayList<>();

        for (int i = 0; i < howManyRecords; i++) {

            Record record = new Record();
            record.set_ID(String.valueOf(generateNumber(1000, 0)));
            record.setNumber(generatePhoneNumber());
            record.setContactID(generateNumber(1000, 500));
            record.setIncoming(generateNumber(1000, 0) >= 500);
            record.setLove(generateNumber(200, 0) >= 100);
            record.setDate(generateDate());

            recordsList.add(record);
        }

        return recordsList;
    }

}
