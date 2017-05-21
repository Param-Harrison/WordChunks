package com.appchamp.wordchunks.ui.main;


interface OnPlayClickListener {

    void startGameActivity(String levelId);

    void showGameFinishedToast();

    void showPacksActivity();
}
