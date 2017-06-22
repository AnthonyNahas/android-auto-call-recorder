package com.anthonynahas.autocallrecorder.events.actionMode;

import android.view.View;

/**
 * Created by anahas on 22.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 22.06.17
 */

public class OnRecordCheckBox {

    public int position;
    public View view;

    public OnRecordCheckBox(int position, View view) {
        this.position = position;
        this.view = view;
    }
}
