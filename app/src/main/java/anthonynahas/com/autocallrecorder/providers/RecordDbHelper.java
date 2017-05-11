package anthonynahas.com.autocallrecorder.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

/**
 * Created by A on 17.04.17.
 */

public class RecordDbHelper {

    private static final String TAG = RecordDbHelper.class.getSimpleName();

    /**
     * Update the column isLove using the content resolver
     *
     * @param context - the used context
     * @param id      - the id of the record
     * @param isLove  - whether the record isLove (favorite)
     * @return the count of updated rows
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
}
