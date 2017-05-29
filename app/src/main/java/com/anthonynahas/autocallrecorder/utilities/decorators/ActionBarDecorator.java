package com.anthonynahas.autocallrecorder.utilities.decorators;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.activities.AppCompatPreferenceActivity;

/**
 * Decorator class that deals with the support action bar in order to
 * display a custom toolbar layour in any AppCompatActivity or AppCompatPreferenceActivity.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 18.05.2017
 */

public class ActionBarDecorator {


    private ActionBar mActionBar;


    /**
     * Setup the custom action bar (toolbar)
     *
     * @param appCompatActivity - the used activity
     */
    public void setup(AppCompatActivity appCompatActivity) {
        ViewGroup rootView = (ViewGroup) appCompatActivity.findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = appCompatActivity.getLayoutInflater().inflate(R.layout.material_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) appCompatActivity.findViewById(R.id.toolbar);
            appCompatActivity.setSupportActionBar(toolbar);
        }

        mActionBar = appCompatActivity.getSupportActionBar();

    }

    /**
     * Setup the custom action bar (toolbar)
     *
     * @param appCompatPreferenceActivity - the used activity
     */
    public void setup(AppCompatPreferenceActivity appCompatPreferenceActivity) {
        ViewGroup rootView = (ViewGroup) appCompatPreferenceActivity.findViewById(R.id.action_bar_root); //id from appcompat

        if (rootView != null) {
            View view = appCompatPreferenceActivity.getLayoutInflater().inflate(R.layout.material_toolbar_layout, rootView, false);
            rootView.addView(view, 0);

            Toolbar toolbar = (Toolbar) appCompatPreferenceActivity.findViewById(R.id.toolbar);
            appCompatPreferenceActivity.setSupportActionBar(toolbar);
        }

        mActionBar = appCompatPreferenceActivity.getSupportActionBar();

    }

    /**
     * Get the initialized custom action bar in order to set a new title, subtitle or more...
     *
     * @return - the initialized custom action bar
     */
    public ActionBar getActionBar() {
        return mActionBar;
    }
}
