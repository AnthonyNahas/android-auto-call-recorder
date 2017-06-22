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
import com.anthonynahas.autocallrecorder.configurations.Constant;
import com.anthonynahas.autocallrecorder.dagger.annotations.ApplicationContext;
import com.anthonynahas.autocallrecorder.events.actionMode.OnRecordCheckBox;
import com.anthonynahas.autocallrecorder.fragments.RecordsFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by anahas on 09.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 09.06.17
 */

public class ActionModeSupport {

    private Context mContext;
    private EventBus mEventBus;
    private Constant mConstant;

    private String mTitle;
    private boolean mIsForBroadcast;
    private ActionBar mActionBar;
    private Toolbar mToolbar;
    private RecordsAdapter mAdapter;

    public ActionModeSupport
            (@ApplicationContext Context mContext,
             EventBus mEventBus,
             Constant mConstant,
             String mTitle,
             boolean mIsForBroadcast,
             Toolbar mToolbar,
             RecordsAdapter mAdapter) {
        this.mEventBus = mEventBus;
        this.mTitle = mTitle;
        this.mIsForBroadcast = mIsForBroadcast;
        this.mToolbar = mToolbar;
        this.mAdapter = mAdapter;

        mEventBus.register(this);
    }

    public ActionModeSupport
            (@ApplicationContext Context mContext,
             EventBus mEventBus,
             Constant mConstant,
             String mTitle,
             boolean mIsForBroadcast) {
        this.mContext = mContext;
        this.mEventBus = mEventBus;
        this.mConstant = mConstant;
        this.mTitle = mTitle;
        this.mIsForBroadcast = mIsForBroadcast;

        mEventBus.register(this);
    }

    public ActionModeSupport(String mTitle,
                             EventBus mEventBus,
                             boolean mIsForBroadcast,
                             ActionBar mActionBar,
                             Toolbar mToolbar,
                             RecordsAdapter mAdapter) {
        this.mEventBus = mEventBus;
        this.mTitle = mTitle;
        this.mIsForBroadcast = mIsForBroadcast;
        this.mActionBar = mActionBar;
        this.mToolbar = mToolbar;
        this.mAdapter = mAdapter;

        mEventBus.register(this);
    }

    public void setActionBar(ActionBar mActionBar) {
        this.mActionBar = mActionBar;
    }

    public void setToolbar(Toolbar mToolbar) {
        this.mToolbar = mToolbar;
    }

    public void setAdapter(RecordsAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSelectionCounter(OnRecordCheckBox event) {
        afterClickCheckBox(event.position, event.view);
    }

    /**
     * Used if the user click on the cell of the recyclerview
     *
     * @param position
     * @param v
     */
    public void onClickCheckBox(int position, View v) {
        CheckBox call_selected = getTargetCheckBox(v);
        boolean isChecked = call_selected.isChecked();
        call_selected.setChecked(!isChecked);
        updateToolbarCounter(!isChecked);
        if (mAdapter != null) {
            mAdapter.getRecordsList().get(position).setSelected(!isChecked);
        }
    }

    /**
     * Used when user clicks of the checkbox its seöf
     *
     * @param position
     * @param v
     */
    public void afterClickCheckBox(int position, View v) {
        CheckBox call_selected = getTargetCheckBox(v);
        boolean isChecked = call_selected.isChecked();
        updateToolbarCounter(isChecked);
        if (mAdapter != null) {
            mAdapter.getRecordsList().get(position).setSelected(isChecked);
        }
    }


    public void enterActionMode(int position, View v) {
        if (!mAdapter.isActionMode()) {
            mAdapter.setActionMode(!mAdapter.isActionMode());
            onClickCheckBox(position, v);
            updateToolbarMenu();
            if (mIsForBroadcast) {
                notifyOnActionMode(true);
            }
        }
    }

    public void cancelActionMode() {
        updateToolbar();
        updateToolbarMenu();

        if (mAdapter != null) {
            mAdapter.setActionMode(false);
            mAdapter.resetCounter();
        }
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
        if (mToolbar != null) {
            if (mAdapter.isActionMode()) {
                mToolbar.inflateMenu(R.menu.action_mode_menu);
            } else {
                mToolbar.getMenu().clear();
            }
        }
    }

    public void updateToolbarCounter(boolean isChecked) {
        if (mAdapter != null) {
            if (isChecked) {
                mAdapter.increaseCounter();
            } else {
                mAdapter.decreaseCounter();
            }
        }
        updateToolbar();
    }

    private CheckBox getTargetCheckBox(View view) {
        return ((CheckBox) view.findViewById(R.id.cb_call_selected));
    }

    private void notifyOnActionMode(boolean state) {
        Intent intent = new Intent(mConstant.BROADCAST_ACTION_ON_ACTION_MODE);
        intent.putExtra(mConstant.ACTION_MODE_SENDER, RecordsFragment.class.getSimpleName());
        intent.putExtra(mConstant.ACTION_MODE_SATE, state);
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
