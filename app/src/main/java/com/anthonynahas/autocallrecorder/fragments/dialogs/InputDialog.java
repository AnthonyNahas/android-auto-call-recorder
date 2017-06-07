package com.anthonynahas.autocallrecorder.fragments.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

import com.anthonynahas.autocallrecorder.utilities.support.DemoRecordSupport;

/**
 * Created by anahas on 07.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 07.06.17
 */

public class InputDialog {

    public static InputDialog newInstance() {
        return new InputDialog();
    }

    public void show(final Activity activity, String title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);

        // Set up the input
        final EditText input = new EditText(activity);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DemoRecordSupport
                        .newInstance()
                        .createDemoRecord(activity, Long.valueOf(input.getText().toString()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
