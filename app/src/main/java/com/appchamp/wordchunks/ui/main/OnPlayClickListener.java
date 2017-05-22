package com.appchamp.wordchunks.ui.main;

/**
 * Callback that listens to the events from the main fragment.
 */
interface OnPlayClickListener {

    void startGameActivity(String levelId);

    void showGameFinishedToast();

    void showPacksActivity();
}
