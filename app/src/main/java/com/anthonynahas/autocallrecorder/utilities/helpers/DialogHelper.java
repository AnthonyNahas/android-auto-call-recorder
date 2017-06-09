package com.anthonynahas.autocallrecorder.utilities.helpers;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.anthonynahas.autocallrecorder.fragments.dialogs.SortDialog;

/**
 * Created by A on 03.04.17.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 03.04.17
 */

public class DialogHelper {

    public static final String TAG = DialogHelper.class.getSimpleName();

    public static final int REQUEST_CODE_FOR_SORT_DIALOG = 1;

    public static void openSortDialog(AppCompatActivity activity) {
        SortDialog sortDialog = new SortDialog();
//        sortDialog.setTargetFragment(fragment, REQUEST_CODE_FOR_SORT_DIALOG);
        sortDialog.show(activity.getSupportFragmentManager(), "sort dialog");
    }

    public static void openSortDialog(AppCompatActivity activity, Fragment fragment) {
        SortDialog sortDialog = new SortDialog();
        sortDialog.setTargetFragment(fragment, REQUEST_CODE_FOR_SORT_DIALOG);
        sortDialog.show(activity.getSupportFragmentManager(), "sort dialog");
    }
}

