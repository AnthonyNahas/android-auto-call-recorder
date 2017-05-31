package com.anthonynahas.recyclerviewfabhandler;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;

/**
 * Created by anahas on 31.05.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 31.05.17
 */

public class FABHandler {

    public static FABHandler newInstance() {
        return new FABHandler();
    }

    public void init(RecyclerView recyclerView, final FloatingActionButton fab) {
        if (recyclerView != null && fab != null) {
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy > 0 || dy < 0 && fab.isShown()) {
                        fab.hide();
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fab.show();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }
    }

}
