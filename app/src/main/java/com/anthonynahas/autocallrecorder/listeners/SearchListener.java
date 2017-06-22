package com.anthonynahas.autocallrecorder.listeners;

import android.os.Bundle;
import android.util.Log;

import com.anthonynahas.autocallrecorder.activities.RecordsActivity;
import com.anthonynahas.autocallrecorder.configurations.Config;
import com.anthonynahas.autocallrecorder.events.search.OnQueryChangedEvent;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.utilities.helpers.ContactHelper;
import com.anthonynahas.autocallrecorder.utilities.helpers.SQLiteHelper;
import com.arlib.floatingsearchview.FloatingSearchView;

import org.apache.commons.lang3.ArrayUtils;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * Created by anahas on 20.06.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 20.06.17
 */

public class SearchListener implements FloatingSearchView.OnQueryChangeListener {

    private static final String TAG = SearchListener.class.getSimpleName();

    private EventBus mEventBus;
    private SQLiteHelper mSQLiteHelper;
    private ContactHelper mContactHelper;

    private Bundle mArguments;

    @Inject
    public SearchListener
            (
                    EventBus mEventBus,
                    SQLiteHelper mSQLiteHelper,
                    ContactHelper mContactHelper
            ) {
        this.mEventBus = mEventBus;
        this.mSQLiteHelper = mSQLiteHelper;
        this.mContactHelper = mContactHelper;
    }

    public FloatingSearchView.OnQueryChangeListener init() {
        return this;
    }

    public FloatingSearchView.OnQueryChangeListener init(Bundle args) {
        mArguments = args;
        return this;
    }

    public void setArguments(Bundle mArguments) {
        this.mArguments = mArguments;
        mEventBus.post(new OnQueryChangedEvent(mArguments));
    }

    @Override
    public void onSearchTextChanged(String oldQuery, String newQuery) {
        Log.d(TAG, "oldQuery = " + oldQuery + " | newQuery = " + newQuery);

        Bundle args = (Bundle) mArguments.clone();

        if (!(newQuery.isEmpty() && oldQuery.length() > newQuery.length() && newQuery.length() == 0)) {
            String contactIDsArguments = mSQLiteHelper
                    .convertArrayToInOperatorArguments(mContactHelper
                            .getContactIDsByName(mContactHelper
                                    .getContactCursorByName(newQuery)));

            String selection = RecordDbContract.RecordItem.COLUMN_NUMBER
                    + " LIKE ?"
                    + " OR "
                    + RecordDbContract.RecordItem.COLUMN_CONTACT_ID
                    + " IN "
                    + contactIDsArguments;
            //+ "= 682";
            String[] selectionArgs = new String[]{"%" + newQuery + "%"};


            String mainSelection = args.getString(Config.args.selection.name());

            if (mainSelection != null && mainSelection.length() > 0) {
                selection += " AND " + mainSelection;
            }

            String[] mainSelectionArgs = args.getStringArray(Config.args.selectionArguments.name());

            if (mainSelectionArgs != null && mainSelectionArgs.length > 0) {
                selectionArgs = ArrayUtils.addAll(selectionArgs, mainSelectionArgs);
            }

            args.putString(RecordsActivity.args.selection.name(), selection);
            args.putStringArray(RecordsActivity.args.selectionArguments.name(), selectionArgs);
        }
        mEventBus.post(new OnQueryChangedEvent(args));
    }
}
