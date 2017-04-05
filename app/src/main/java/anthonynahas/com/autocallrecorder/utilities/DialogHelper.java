package anthonynahas.com.autocallrecorder.utilities;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import anthonynahas.com.autocallrecorder.fragments.SortDialogFragment;

/**
 * Created by A on 03.04.17.
 */

public class DialogHelper {

    public static final String TAG = DialogHelper.class.getSimpleName();

    public static final int REQUEST_CODE_FOR_SORT_DIALOG = 1;

    public static void openSortDialog(AppCompatActivity activity, Fragment fragment){
        Log.d(TAG, "MenuItem = sort");
        SortDialogFragment sortDialogFragment = new SortDialogFragment();
        sortDialogFragment.setTargetFragment(fragment, REQUEST_CODE_FOR_SORT_DIALOG);
        sortDialogFragment.show(activity.getSupportFragmentManager(), "sort dialog");
    }
}
