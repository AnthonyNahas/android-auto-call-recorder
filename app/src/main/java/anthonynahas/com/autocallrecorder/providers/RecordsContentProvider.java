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
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by A on 29.04.16.
 * @author Anthony Nahas
 *
 */
public class RecordsContentProvider extends ContentProvider {

    private static final String TAG = RecordsContentProvider.class.getSimpleName();

    private RecordsSQLiteOpenHelper mRecordsSQLiteOpenHelper;
    private SQLiteDatabase mDB;

    private static final UriMatcher sUriMatcher;
    private static final int TABLE_ITEMS = 0;
    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;


    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(RecordDbContract.AUTHORITY, RecordDbContract.RecordItem.TABLE_NAME
                + "/offset/" + "#", TABLE_ITEMS);
        sUriMatcher.addURI(RecordDbContract.AUTHORITY, "/" + RecordDbContract.PATH, ALL_ROWS);
        sUriMatcher.addURI(RecordDbContract.AUTHORITY, "/" + RecordDbContract.PATH + "/#" +
                RecordDbContract.RecordItem.COLUMN_ID, SINGLE_ROW);
        Log.d(TAG, "static sUriMatcher)");
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate() - contentProvider");
        mRecordsSQLiteOpenHelper = new RecordsSQLiteOpenHelper(getContext());
        mDB = mRecordsSQLiteOpenHelper.getWritableDatabase();

        return mDB != null;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query1()");
        //create a new querybuilder for table with birthdays
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(RecordDbContract.RecordItem.TABLE_NAME);


        if (sUriMatcher.match(uri) == SINGLE_ROW) {
            queryBuilder.appendWhere(RecordDbContract.RecordItem.COLUMN_ID + " = "
                    + uri.getPathSegments().get(1));
        }

        Cursor cursor = queryBuilder.query(mDB, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "query2()");
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
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "onInsert1()");

        long rowID = mDB.insert(RecordDbContract.RecordItem.TABLE_NAME, "", values);
        Log.d(TAG, "r = " + rowID);

        if (rowID > 0) {
            //create corresponding URI for the created row
            Uri insertedIdUri = ContentUris.withAppendedId(RecordDbContract.CONTENT_URL, rowID);

            //notify resolver for data change
            getContext().getContentResolver().notifyChange(insertedIdUri, null);
            Log.d(TAG, "onInsert2()");
            //return new URO for item
            return insertedIdUri;
        } else {
            Log.e(TAG, "ERROR onInsert()");
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

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
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    public Cursor retrieve(String searchItem) {

        String[] columns = {RecordDbContract.RecordItem.COLUMN_ID,
                RecordDbContract.RecordItem.COLUMN_NUMBER,
                RecordDbContract.RecordItem.COLUMN_CONTACTID,
                RecordDbContract.RecordItem.COLUMN_DATE,
                RecordDbContract.RecordItem.COLUMN_DURATION,
                RecordDbContract.RecordItem.COLUMN_INCOMING,
                RecordDbContract.RecordItem.COLUMN_SIZE};
        Cursor cursor = null;

        if (searchItem != null && searchItem.length() > 0) {
            String sql = "SELECT * FROM" + RecordDbContract.RecordItem.TABLE_NAME + " WHERE " + RecordDbContract.RecordItem.COLUMN_NUMBER + " LIKE '%" + searchItem + "%'";
            cursor = mDB.rawQuery(sql, null);
            return cursor;
        }

        cursor = mDB.query(RecordDbContract.RecordItem.TABLE_NAME, columns, null, null, null, null, null, null);

        return cursor;

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
                + " (" + RecordDbContract.RecordItem.COLUMN_ID + " TEXT NOT NULL, " +
                RecordDbContract.RecordItem.COLUMN_NUMBER + " TEXT NOT NULL, " +
                RecordDbContract.RecordItem.COLUMN_CONTACTID + " LONG, " +
                RecordDbContract.RecordItem.COLUMN_DATE + " LONG, " +
                RecordDbContract.RecordItem.COLUMN_SIZE + " INTEGER, " +
                RecordDbContract.RecordItem.COLUMN_DURATION + " INTEGER, " +
                RecordDbContract.RecordItem.COLUMN_INCOMING + " INTEGER)";

        //drop table query
        private static final String DROP_TABLE = "DROP TABLE IF IT EXISTS " +
                RecordDbContract.RecordItem.TABLE_NAME + ";";

        /**
         * crate table
         *
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
            Log.d(TAG, "onCreate() DB");

        }

        /**
         * update table
         *
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(TAG, "onUpgrade() DB");
            db.execSQL(DROP_TABLE);
            db.execSQL(CREATE_TABLE);
        }
    }
}
