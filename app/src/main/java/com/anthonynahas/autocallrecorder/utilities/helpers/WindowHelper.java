package com.anthonynahas.autocallrecorder.utilities.helpers;

import android.view.Window;

/**
 * Created by anahas on 06.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 06.06.2017
 */

/**
 * Simple class helper that deal with the window object
 */
public class WindowHelper {

    /**
     * Enhance the window by setting a custom dim amout and
     * a background!
     *
     * @param window - the current used window (getDialog.getwindow....)
     */
    public static void init(Window window) {
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setDimAmount(0.9f);
        }
        //window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
