package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;

/**
 * Created by A on 10.06.16.
 */
public class DropBoxHelper {

    private static final String TAG = DropBoxHelper.class.getSimpleName();

    public final static String DROPBOX_FILE_DIR = "/DropboxDemo/";
    public final static String DROPBOX_NAME = "dropbox_prefs";
    private final static Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;
    final static public String DROPBOX_APP_KEY = "32fcvkt4b7ym4sv";
    final static public String DROPBOX_APP_SECRET = "dj1ac0hwabq0f71";
    static final int REQUEST_LINK_TO_DBX = 0;

    private static final String ACCOUNT_PREFS_NAME = "prefs";
    private static final String ACCESS_KEY_NAME = "ACCESS_KEY";
    private static final String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    private DropboxAPI<AndroidAuthSession> mApi;
    private boolean mLoggedIn;
    private Context mContext;

    public DropBoxHelper(Context context) {
        mContext = context;
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<>(session);
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(DROPBOX_APP_KEY, DROPBOX_APP_SECRET);

        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session);
        return session;
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void loadAuth(AndroidAuthSession session) {
        SharedPreferences prefs = mContext.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) return;

        if (key.equals("oauth2:")) {
            // If the key is set to "oauth2:", then we can assume the token is for OAuth 2.
            session.setOAuth2AccessToken(secret);
        } else {
            // Still support using old OAuth 1 tokens.
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    public DropboxAPI<AndroidAuthSession> getApi() {
        return mApi;
    }

    public static String getDropboxFileDir() {
        return DROPBOX_FILE_DIR;
    }
}
