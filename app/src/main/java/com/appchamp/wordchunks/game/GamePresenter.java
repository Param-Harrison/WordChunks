package com.appchamp.wordchunks.game;

import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Listens to user actions from the UI ({@link GameFragment}), retrieves the data and updates the
 * UI as required.
 */
public class GamePresenter implements GameContract.Presenter {

    private final GameContract.View gameView;


    public GamePresenter(@NonNull GameContract.View gameView) {
        this.gameView = checkNotNull(gameView, "gameView cannot be null!");

        gameView.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
