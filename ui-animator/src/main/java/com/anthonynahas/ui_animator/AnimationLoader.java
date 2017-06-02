package com.anthonynahas.ui_animator;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by anahas on 02.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @see http://www.journaldev.com/9481/android-animation-example
 * @since 02.06.17
 */

public class AnimationLoader {


    private Context mContext;

    public AnimationLoader(Context mContext) {
        this.mContext = mContext;
    }

    public static Animation get(Context context, int animation) {
        return AnimationUtils.loadAnimation(context, animation);
    }

}
