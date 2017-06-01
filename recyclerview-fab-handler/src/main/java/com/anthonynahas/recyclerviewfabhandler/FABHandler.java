package com.anthonynahas.recyclerviewfabhandler;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by anahas on 31.05.2017.
 *
 * @author Anthony Nahas
 * @version 1.0
 * @since 31.05.17
 */

public class FABHandler {

    private static final String TAG = FABHandler.class.getSimpleName();

    public static FABHandler newInstance() {
        return new FABHandler();
    }

    public void init(@NonNull RecyclerView recyclerView,
                     @NonNull final FloatingActionButton fabMain,
                     final FloatingActionButton fabScrollTo) {

        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        if (fabScrollTo != null) {
            fabScrollTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    layoutManager.scrollToPosition(0);
                }
            });
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fabMain.isShown()) {
                    fabMain.hide();
                    if (fabScrollTo != null) {
                        fabScrollTo.show();
                    }
                }
                if (fabScrollTo != null) {
                    PointF pointF = layoutManager.computeScrollVectorForPosition(0);
                    if (pointF.equals(0, 1) && fabScrollTo.isShown()) {
                        fabScrollTo.hide();
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabMain.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

}
