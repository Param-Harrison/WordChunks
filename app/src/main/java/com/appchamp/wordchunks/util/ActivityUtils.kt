package com.appchamp.wordchunks.util

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * This provides methods to help Activities load their UI.
 */
object ActivityUtils {

    /**
     * The `fragment` is added to the container view with id `frameId`.
     * The operation is performed by the `fragmentManager`.
     */
    fun addFragment(fragmentManager: FragmentManager, fragment: Fragment, @IdRes frameId: Int) {
        val transaction = fragmentManager.beginTransaction()
        transaction.add(frameId, fragment)
        transaction.commit()
    }

    fun replaceFragment(fragmentManager: FragmentManager, fragment: Fragment, @IdRes frameId: Int) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(frameId, fragment)
        transaction.commit()
    }
}