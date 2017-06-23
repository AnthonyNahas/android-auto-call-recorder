package com.anthonynahas.autocallrecorder.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.anthonynahas.autocallrecorder.R;
import com.anthonynahas.autocallrecorder.adapters.RecordsAdapter;
import com.anthonynahas.autocallrecorder.configurations.C;
import com.anthonynahas.autocallrecorder.dagger.annotations.android.HandlerToWaitForLoading;
import com.anthonynahas.autocallrecorder.dagger.annotations.keys.activities.RecordsActivityKey;
import com.anthonynahas.autocallrecorder.events.loading.OnLoadingBegin;
import com.anthonynahas.autocallrecorder.events.loading.OnLoadingDone;
import com.anthonynahas.autocallrecorder.providers.RecordDbContract;
import com.anthonynahas.autocallrecorder.providers.RecordsContentProvider;
import com.anthonynahas.autocallrecorder.utilities.support.ActionModeSupport;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by anahas on 23.06.2017.
 *
 * @author Anthony Nahas
 * @since 23.06.2017
 */

public abstract class AppActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @Inject
    @RecordsActivityKey
    ActionModeSupport mActionModeSupport;

    @Inject
    @HandlerToWaitForLoading
    Handler mHandlerToWait;

    @Inject
    EventBus mEventBus;

    @Inject
    RecordsAdapter mAdapter;

    @Inject
    C c;

    private int mLoaderManagerID;
    private Bundle mArguments;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setTheme(@StyleRes int resid) {
        super.setTheme(resid);
    }

    @Nullable
    @Override
    public ActionBar getSupportActionBar() {
        return super.getSupportActionBar();
    }

    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mEventBus.unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        mActionModeSupport.inflateMenu(getMenuInflater(), menu, R.menu.action_mode_menu);
        return super.onCreateOptionsMenu(menu);
    }

    // requirements:
    // id should be != 1 and args != null
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mEventBus.post(new OnLoadingBegin());
        if (id != -1 && args != null) {

            String[] projection = args.getStringArray(c.projection);
            String selection = args.getString(c.selection);
            String[] selectionArgs = args.getStringArray(c.selectionArguments);
            String sort = args.getString(c.sort);
            String groupBy = args.getString(c.groupBy);
            int limit = args.getInt(c.limit);
            int offset = args.getInt(c.offset);

            Uri uri = RecordDbContract.CONTENT_URL
                    .buildUpon()
                    .appendQueryParameter(RecordsContentProvider.QUERY_PARAMETER_LIMIT,
                            String.valueOf(limit))
                    .appendQueryParameter(RecordsContentProvider.QUERY_PARAMETER_OFFSET,
                            String.valueOf(offset))
                    .build();

            return new CursorLoader(this, uri, projection, selection, selectionArgs, sort);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mEventBus.post(new OnLoadingDone());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
