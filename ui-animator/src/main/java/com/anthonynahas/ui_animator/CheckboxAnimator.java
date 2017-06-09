package com.anthonynahas.ui_animator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by anahas on 06.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 06.06.17
 */

public class CheckboxAnimator {

    private CheckBox mCheckBox;

    public CheckboxAnimator(CheckBox mCheckBox) {
        this.mCheckBox = mCheckBox;
    }

    public static CheckboxAnimator newInstance(CheckBox checkBox) {
        return new CheckboxAnimator(checkBox);
    }

    public void toRight() {
        mCheckBox.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofFloat(mCheckBox, "translationX", 10f);
        animation.setDuration(500);
        animation.start();
    }

    public void toLeft() {
        ObjectAnimator animation = ObjectAnimator.ofFloat(mCheckBox, "translationX", -10f);
        animation.setDuration(500);
        animation.start();
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCheckBox.setVisibility(View.GONE);
                mCheckBox.setChecked(false);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

}
