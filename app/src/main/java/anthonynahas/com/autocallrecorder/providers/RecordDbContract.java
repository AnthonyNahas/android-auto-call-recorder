package anthonynahas.com.autocallrecorder.providers;

import android.net.Uri;

/**
 * this class implement some structure and values to be used for the content provider
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 29.4.16
 */
public class RecordDbContract {
    //db
    public static final String DATABASE_NAME = "Records.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * The authority of the Auto Call Recorder app provider
     */
    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "anthonynahas.com.autocallrecorder.providers.RecordsContentProvider";
    public static final String PATH = RecordItem.TABLE_NAME;
    public static final String URL = SCHEME + AUTHORITY + "/" + PATH;


    public static final String URI_OFFSET = SCHEME + AUTHORITY + "/" + PATH + "/limit/";

    //Uri.parse("content://" + RecordDbContract.AUTHORITY + "/" +
    //RecordDbContract.RecordItem.TABLE_NAME + "/limit/" + limit);

    /**
     * THe content URI for the top-level BlogItem authority
     */
    public static final Uri CONTENT_URL = Uri.parse(URL);

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd";


    /**
     * this class implement the structure of a record item in the database
     *
     * @author Anthony Nahas
     * @version 1.1
     * @since 29.4.16
     */
    public static abstract class RecordItem extends RecordDbContract {

        //table
        public static final String TABLE_NAME = "Records";

        //columns
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_CONTACT_ID = "contactid";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_INCOMING = "incoming";
        public static final String COLUMN_IS_LOVE = "isLove";
        public static String[] ALL_COLUMNS = new String[]
                {
                        COLUMN_ID,
                        COLUMN_NUMBER,
                        COLUMN_CONTACT_ID,
                        COLUMN_DATE,
                        COLUMN_SIZE,
                        COLUMN_DURATION,
                        COLUMN_INCOMING,
                        COLUMN_IS_LOVE
                };
    }
}
