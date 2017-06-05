package com.appchamp.wordchunks.ui.game

import android.content.Context
import android.support.v7.widget.GridLayoutManager

/*
 * Disables scrolling in grids.
 */
class CustomGridLayoutManager(context: Context, spanCount: Int) :
        GridLayoutManager(context, spanCount) {

    override fun canScrollVertically(): Boolean {
        return false
    }
}
