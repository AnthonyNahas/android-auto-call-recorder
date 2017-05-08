package anthonynahas.com.autocallrecorder.utilities.decoraters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import anthonynahas.com.autocallrecorder.providers.RecordDbContract;
import anthonynahas.com.autocallrecorder.providers.RecordsQueryHandler;
import anthonynahas.com.autocallrecorder.utilities.helpers.ContactHelper;

/**
 * This class is responsible to mock and generate demo records for test
 * and debug purposes.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 25.04.2017
 */

public class DemoRecordSupport {

    private static final String TAG = DemoRecordSupport.class.getSimpleName();

    //todo generatePics with lru cache

    public static DemoRecordSupport getInstance() {
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
        values.put(RecordDbContract.RecordItem.COLUMN_INCOMING, generateNumber(1, 0));
        values.put(RecordDbContract.RecordItem.COLUMN_SIZE, generateNumber(100, 1));
        values.put(RecordDbContract.RecordItem.COLUMN_DURATION, generateNumber(800, 400));
        RecordsQueryHandler.getInstance(context.getContentResolver())
                .startInsert(RecordsQueryHandler.INSERT_DEMO, null, RecordDbContract.CONTENT_URL, values);

        //context.getContentResolver().insert(RecordDbContract.CONTENT_URL, values);
        Log.d(TAG, "contentResolver inserted dummy record");
    }

    public void createDemoRecord(Context context) {

        ArrayList<String> contactListNumbers = new ArrayList<>();

        // retrieve all contacts
        Cursor cursor = ContactHelper.getContactCursorByName(context.getContentResolver(), "");

        if (cursor.moveToNext()) {
            do {
                //long contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                //long contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                // TODO: 05.05.17 control if it works with api < 24
                long contact_id;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup.CONTACT_ID));
                } else {
                    contact_id = cursor.getLong(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                }
                String contact_display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contact_number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactListNumbers.add(contact_number);

                Log.d(TAG, "phonelookup_contact_id = "
                        + contact_id
                        + " -- phonelookup_id ---> "
                        + ContactHelper.getContactID(context.getContentResolver(), contact_number)
                        + " --> name = "
                        + contact_display_name
                        + " --> number = "
                        + contact_number);
            } while (cursor.moveToNext());

            cursor.close();
        }

        if (contactListNumbers.size() == 0) {
            createDummyRecord(context);
            return;
        }

        int random = generateNumber(contactListNumbers.size() - 1, 0);

        ContentValues values = new ContentValues();
        values.put(RecordDbContract.RecordItem.COLUMN_ID, String.valueOf(generateNumber(10000, 5000)));
        values.put(RecordDbContract.RecordItem.COLUMN_DATE, generateDate());
        values.put(RecordDbContract.RecordItem.COLUMN_NUMBER, contactListNumbers.get(random));
        values.put(RecordDbContract.RecordItem.COLUMN_INCOMING, generateNumber(1, 0));
        values.put(RecordDbContract.RecordItem.COLUMN_SIZE, generateNumber(100, 1));
        values.put(RecordDbContract.RecordItem.COLUMN_DURATION, generateNumber(600, 0));

        RecordsQueryHandler.getInstance(context.getContentResolver())
                .startInsert(RecordsQueryHandler.INSERT_DEMO, null, RecordDbContract.CONTENT_URL, values);

        Log.d(TAG, "contentResolver inserted demo record");
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

}
