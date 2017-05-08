package anthonynahas.com.autocallrecorder.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.recyclerview.BuildConfig;
import android.util.Log;

/**
 * Created by A on 29.04.16.
 *
 * @author Anthony Nahas
 */
public class RecordsContentProvider extends ContentProvider {

    private static final String TAG = RecordsContentProvider.class.getSimpleName();

    public static final String QUERY_PARAMETER_LIMIT = "limit";
    public static final String QUERY_PARAMETER_OFFSET = "offset";

    private RecordsSQLiteOpenHelper mRecordsSQLiteOpenHelper;
    private SQLiteDatabase mDB;

    private static final UriMatcher sUriMatcher;
    private static final int TABLE_ITEMS = 0;
    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;


    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(RecordDbContract.AUTHORITY, RecordDbContract.RecordItem.TABLE_NAME
                + "/limit/" + "#", TABLE_ITEMS);
        sUriMatcher.addURI(RecordDbContract.AUTHORITY, "/" + RecordDbContract.PATH, ALL_ROWS);
        sUriMatcher.addURI(RecordDbContract.AUTHORITY, "/" + RecordDbContract.PATH + "/*", SINGLE_ROW);
        Log.d(TAG, "static sUriMatcher)");
    }

    public static Uri urlForItems(int limit) {
        return Uri.parse("content://" + RecordDbContract.AUTHORITY + "/" +
                RecordDbContract.RecordItem.TABLE_NAME + "/limit/" + limit);
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate() - contentProvider");
        mRecordsSQLiteOpenHelper = new RecordsSQLiteOpenHelper(getContext());
        mDB = mRecordsSQLiteOpenHelper.getWritableDatabase();

        return mDB != null;

    }


    @Override
    synchronized public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query()");
        //create a new querybuilder for table with records
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(RecordDbContract.RecordItem.TABLE_NAME);

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {


            case TABLE_ITEMS:
                queryBuilder.setTables(RecordDbContract.RecordItem.TABLE_NAME);
                int intOffset = Integer.parseInt(uri.getLastPathSegment());

                String limitArg = intOffset + ", " + 30;
                Log.d(TAG, "query: " + limitArg);
                cursor = queryBuilder.query(mDB, projection, selection, selectionArgs, null, null, sortOrder, limitArg);
                //logCursor(cursor);
                break;

            case ALL_ROWS:
                Log.d(TAG, "all rows");
                //This line will let CursorLoader know about any data change on "uri" ,
                // So that data will be reloaded to CursorLoader

                //Make certain that the URI you register the cursor on,
                //and the URI you notify the cursor on are the same.

                String limit = uri.getQueryParameter(QUERY_PARAMETER_LIMIT);
                String offset = uri.getQueryParameter(QUERY_PARAMETER_OFFSET);
                String limitString = offset + "," + limit;
                // TODO: 07.05.17 try and catch --> runtimeException ex: selectionArgs > selection ? or no such column found...
                cursor = queryBuilder.query(mDB, projection, selection, selectionArgs, null, null, sortOrder, limitString);
                break;

            case SINGLE_ROW:
                queryBuilder.appendWhere(RecordDbContract.RecordItem.COLUMN_ID + " = "
                        + uri.getPathSegments().get(1));
                cursor = queryBuilder.query(mDB, projection, selection, selectionArgs, null, null, sortOrder);
                break;


            default:
                throw new IllegalArgumentException("uri not recognized!");
                //break;
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        //logCursor(cursor);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType()");
        switch (sUriMatcher.match(uri)) {

            case ALL_ROWS:
                Log.d(TAG, "getType in switch()");
                return RecordDbContract.CONTENT_TYPE + RecordDbContract.AUTHORITY + "/" + RecordDbContract.RecordItem.TABLE_NAME;
            case SINGLE_ROW:
                return RecordDbContract.CONTENT_ITEM_TYPE + RecordDbContract.AUTHORITY + "/" + RecordDbContract.RecordItem.TABLE_NAME;
            case TABLE_ITEMS:
                return BuildConfig.APPLICATION_ID + ".item";
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public synchronized Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "onInsert1()");

        long rowID = mDB.insert(RecordDbContract.RecordItem.TABLE_NAME, "", values);
        Log.d(TAG, "r = " + rowID);

        if (rowID > 0) {
            //create corresponding URI for the created row
            Uri insertedIdUri = ContentUris.withAppendedId(RecordDbContract.CONTENT_URL, rowID);
            //Uri retUri = ContentUris.withAppendedId(uri, result);

            //notify resolver for data change
            getContext().getContentResolver().notifyChange(insertedIdUri, null);
            Log.d(TAG, "onInsert2()");
            //return new URI for item
            return insertedIdUri;
        } else {
            Log.e(TAG, "ERROR onInsert()");
            return null;
        }
    }

    @Override
    public synchronized int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        String mSelectionCorrected = selection;

        switch (sUriMatcher.match(uri)) {
            case ALL_ROWS:
                break;
            case SINGLE_ROW:

                //if single row, add additional selection filter for the id
                //selection = selection + BirthdayDbContract.Birthday.COLUMN_ID + " = " + uri.getLastPathSegment();

                //edited: only use the where ID=# clause and do not append to other clauses -> redundant and may cause errors
                mSelectionCorrected = RecordDbContract.RecordItem.COLUMN_ID + " = '" + uri.getLastPathSegment() + "'";
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //delete the item
        int count = mDB.delete(RecordDbContract.RecordItem.TABLE_NAME, mSelectionCorrected, selectionArgs);
        //notify the resolver of data change
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public synchronized int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "onUpdate()");

        switch (sUriMatcher.match(uri)) {
            case ALL_ROWS:
                break;
            case SINGLE_ROW:
                break;
        }

        // update the item
        int rowUpdated = mDB.update(RecordDbContract.RecordItem.TABLE_NAME, values, selection, selectionArgs);
        Log.d(TAG, "r = " + rowUpdated);

        // notify the resolver of data change
        getContext().getContentResolver().notifyChange(uri, null);

        return rowUpdated;
    }

    public Cursor retrieve(String searchItem) {

        String[] columns = {RecordDbContract.RecordItem.COLUMN_ID,
                RecordDbContract.RecordItem.COLUMN_NUMBER,
                RecordDbContract.RecordItem.COLUMN_CONTACT_ID,
                RecordDbContract.RecordItem.COLUMN_DATE,
                RecordDbContract.RecordItem.COLUMN_DURATION,
                RecordDbContract.RecordItem.COLUMN_INCOMING,
                RecordDbContract.RecordItem.COLUMN_SIZE};

        Cursor cursor;

        if (searchItem != null && searchItem.length() > 0) {
            String sql = "SELECT * FROM" + RecordDbContract.RecordItem.TABLE_NAME + " WHERE " + RecordDbContract.RecordItem.COLUMN_NUMBER + " LIKE '%" + searchItem + "%'";
            cursor = mDB.rawQuery(sql, null);
            return cursor;
        }

        cursor = mDB.query(RecordDbContract.RecordItem.TABLE_NAME, columns, null, null, null, null, null, null);

        return cursor;

    }

    private void logCursor(Cursor cursor) {
        if (cursor.moveToFirst()) {
            do {
                String _id = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_ID));
                long contact_id = cursor.getLong(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_CONTACT_ID));
                int isLove = cursor.getInt(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_IS_LOVE));
                Log.d(TAG, "_id = " + _id + " " +  "contact id = " + contact_id + " --> isLove = " + isLove);
                //String date = cursor.getString(cursor.getColumnIndex(RecordDbContract.RecordItem.COLUMN_DATE));
                //Log.d(TAG, "date = " + date);
            } while (cursor.moveToNext());
        }
        cursor.moveToFirst();
        //cursor.close();
    }

    /**
     * this class implement a custom sqlite open helper to handle with the database
     *
     * @author Anthony Nahas
     * @version 1.0
     * @since 29.4.16
     */
    private class RecordsSQLiteOpenHelper extends SQLiteOpenHelper {

        public RecordsSQLiteOpenHelper(Context context) {
            super(context, RecordDbContract.DATABASE_NAME, null, RecordDbContract.DATABASE_VERSION);
            Log.d(TAG, "dbConstructor");
        }


        //create table query
        public static final String CREATE_TABLE = "CREATE TABLE " + RecordDbContract.RecordItem.TABLE_NAME
                + " ("
                + RecordDbContract.RecordItem.COLUMN_ID + " TEXT NOT NULL, "
                + RecordDbContract.RecordItem.COLUMN_NUMBER + " TEXT NOT NULL, "
                + RecordDbContract.RecordItem.COLUMN_CONTACT_ID + " LONG, "
                + RecordDbContract.RecordItem.COLUMN_DATE + " LONG, "
                + RecordDbContract.RecordItem.COLUMN_SIZE + " INTEGER, "
                + RecordDbContract.RecordItem.COLUMN_DURATION + " INTEGER, "
                + RecordDbContract.RecordItem.COLUMN_INCOMING + " INTEGER, "
                + RecordDbContract.RecordItem.COLUMN_IS_LOVE + " INTEGER DEFAULT 0"
                + ")";

        //drop table query
        private static final String DROP_TABLE = "DROP TABLE IF IT EXISTS " +
                RecordDbContract.RecordItem.TABLE_NAME + ";";

        /**
         * crate table
         *
         * @param db - the used db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            Log.d(TAG, "onCreate() DB");

        }

        /**
         * update table.
         * This block will be only run if the version of the db
         * has been increased! --> newVersion > oldVersion
         *
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade() DB");
            // TODO: 07.05.17 db migration --> query - store in cursor - migrate
            // TODO: 08.05.17 android.database.sqlite.SQLiteException --> try and catch
            db.execSQL(DROP_TABLE);
            db.execSQL(CREATE_TABLE);
        }
    }
}
