package com.appchamp.wordchunks.game;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;

/*
 * Disables scrolling in grids.
 */
public class CustomGridLayoutManager extends GridLayoutManager {

    CustomGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
