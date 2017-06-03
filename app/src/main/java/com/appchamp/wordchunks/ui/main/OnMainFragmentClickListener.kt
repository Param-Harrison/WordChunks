package com.appchamp.wordchunks.ui.main

/**
 * Callback that listens to the events from the main fragment.
 */
internal interface OnMainFragmentClickListener {

    fun startGameActivity(levelId: String?)

    fun showGameFinishedFragment()

    fun showPacksActivity()

    fun showSlidingMenu()
}
