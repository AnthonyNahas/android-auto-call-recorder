package com.anthonynahas.autocallrecorder.utilities.support;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.CheckBox;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.adapters.RecordsAdapter;
import com.anthonynahas.autocallrecorder.classes.Res;
import com.anthonynahas.autocallrecorder.fragments.RecordsFragment;

/**
 * Created by anahas on 09.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 09.06.17
 */

public class ActionModeSupport {

    private String mTitle;
    private boolean mIsForBroadcast;
    private Context mContext;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private RecordsAdapter mAdapter;

    public ActionModeSupport(String mTitle,
                             boolean mIsForBroadcast,
                             Context mContext,
                             Toolbar mToolbar,
                             RecordsAdapter mAdapter) {
        this.mTitle = mTitle;
        this.mIsForBroadcast = mIsForBroadcast;
        this.mContext = mContext;
        this.mToolbar = mToolbar;
        this.mAdapter = mAdapter;
    }

    public ActionModeSupport(String mTitle,
                             boolean mIsForBroadcast,
                             Context mContext,
                             ActionBar mActionBar,
                             Toolbar mToolbar,
                             RecordsAdapter mAdapter) {
        this.mTitle = mTitle;
        this.mIsForBroadcast = mIsForBroadcast;
        this.mContext = mContext;
        this.mActionBar = mActionBar;
        this.mToolbar = mToolbar;
        this.mAdapter = mAdapter;
    }

    public void handleCheckBoxSelectionInActionMode(int position, View v) {
        CheckBox call_selected = getTargetCheckBox(v);
        boolean isChecked = call_selected.isChecked();
        call_selected.setChecked(!isChecked);
        mAdapter.getRecordsList().get(position).setSelected(!isChecked);
        updateToolbarCounter(isChecked);
    }

    public void enterActionMode(int position, View v) {
        if (!mAdapter.isActionMode()) {
            mAdapter.setActionMode(!mAdapter.isActionMode());
            handleCheckBoxSelectionInActionMode(position, v);
            updateToolbarMenu();
            if (mIsForBroadcast) {
                notifyOnActionMode(true);
            }
        }
    }

    public void cancelActionMode() {
        mAdapter.setActionMode(false);
        mAdapter.resetCounter();
        updateToolbar();
        updateToolbarMenu();
        if (mIsForBroadcast) {
            notifyOnActionMode(false);
        }
    }

    public boolean inflateMenu(MenuInflater inflater, Menu menu, int id) {
        if (mAdapter.isActionMode()) {
            inflater.inflate(id, menu);
            return true;
        }
        return false;
    }

    public void updateToolbarMenu() {
        if (mAdapter.isActionMode()) {
            mToolbar.inflateMenu(R.menu.action_mode_menu);
        } else {
            mToolbar.getMenu().clear();
        }
    }

    public void updateToolbarCounter(boolean isChecked) {
        if (isChecked) {
            mAdapter.decreaseCounter();
        } else {
            mAdapter.increaseCounter();
        }
        updateToolbar();
    }

    private CheckBox getTargetCheckBox(View view) {
        return ((CheckBox) view.findViewById(R.id.call_selected));
    }

    private void notifyOnActionMode(boolean state) {
        Intent intent = new Intent(Res.BROADCAST_ACTION_ON_ACTION_MODE);
        intent.putExtra(Res.ACTION_MODE_SENDER, RecordsFragment.class.getSimpleName());
        intent.putExtra(Res.ACTION_MODE_SATE, state);
        LocalBroadcastManager.getInstance(mContext)
                .sendBroadcast(intent);
    }

    private void updateToolbar() {
        String title;
        if (mAdapter.isActionMode()) {
            if (mAdapter.getCounter() == 0) {
                title = mContext.getResources().getString(R.string.toolbar_action_mode_text);
            } else {
                title = String.valueOf(mAdapter.getCounter());
            }
        } else {
            title = mTitle;
        }

        if (mIsForBroadcast) {
            // TODO: 09.06.2017
        } else {
            mActionBar.setTitle(title);
        }
    }
}
