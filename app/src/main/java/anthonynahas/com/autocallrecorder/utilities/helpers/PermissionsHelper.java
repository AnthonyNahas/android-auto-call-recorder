package anthonynahas.com.autocallrecorder.utilities.helpers;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * Class that deals with permissions at Runtime for Android OS
 * beginning in Android 6.0 (API level 23).
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 30.03.2017
 */

public class PermissionsHelper {

    private Context mContext;
    private int mRequestCode = 1215;
    /**
     * Once READ_EXTERNAL_STORAGE is granted, application will also grant WRITE_EXTERNAL_STORAGE permission.
     */
    private String[] mRequiredPermissions =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS
            };

    public PermissionsHelper(Context context) {
        mContext = context;
    }

    public String[] getRequiredPermissions() {
        return mRequiredPermissions;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkPermission(final String permission, final int requestCode) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, permission)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Call phone permission is necessary to make records!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{permission}, requestCode);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{permission}, requestCode);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void requestAllPermissions() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            int isPermissionsGranted = 0;
            for (String mRequiredPermission : mRequiredPermissions) {
                isPermissionsGranted += ContextCompat.checkSelfPermission(mContext, mRequiredPermission);
            }
            if (isPermissionsGranted != PackageManager.PERMISSION_GRANTED) {
                boolean shouldShowRequestPermissionRationale = false;
                for (String mRequiredPermission : mRequiredPermissions) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext, mRequiredPermission)) {
                        shouldShowRequestPermissionRationale = true;
                        break;
                    }
                }
                if (shouldShowRequestPermissionRationale) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Call phone permission is necessary to make records!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) mContext, mRequiredPermissions, mRequestCode);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) mContext, mRequiredPermissions, mRequestCode);
                }
            }
        }
    }

}
