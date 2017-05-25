package com.appchamp.wordchunks.ui.game;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/*
 * Disables scrolling in grids.
 */
public class CustomGridLayoutManager extends GridLayoutManager {

    public CustomGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
