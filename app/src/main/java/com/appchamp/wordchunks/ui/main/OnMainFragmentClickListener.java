package com.appchamp.wordchunks.ui.main;

/**
 * Callback that listens to the events from the main fragment.
 */
interface OnMainFragmentClickListener {

    void startGameActivity(String levelId);

    void showGameFinishedFragment();

    void showPacksActivity();

    void showSlidingMenu();
}
