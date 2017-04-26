package anthonynahas.com.autocallrecorder.providers;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Created by anahas on 26.04.2017.
 */

public class RecordsQueryHandler extends AsyncQueryHandler {

    private static final String TAG = RecordsQueryHandler.class.getSimpleName();

    public static final int INSERT = 0;
    public static final int INSERT_DEMO = 1;

    public static final int UPDATE_IS_LOVE = 0;

    private Cursor mCursor;


    public RecordsQueryHandler(ContentResolver contentResolver) {
        super(contentResolver);
    }

    public static RecordsQueryHandler getInstance(ContentResolver contentResolver) {
        return new RecordsQueryHandler(contentResolver);
    }


    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        Log.d(TAG, "onInsertComplete: " + token + " " + cookie + " " + uri);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        Log.d(TAG, "onUpdateComplete: " + token + " " + cookie + " " + result);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        Log.d(TAG, "onDeleteComplete: " + token + " " + cookie + " " + result);
    }

}
