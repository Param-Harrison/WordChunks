package com.appchamp.wordchunks.data;

import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Pack;

import io.realm.Realm;

import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_CURRENT;
import static com.appchamp.wordchunks.util.Constants.PACK_STATE_CURRENT;


public final class GameRealmHelper {

    private GameRealmHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Initializes the game state in the beginning of the game.
     */
    public static void initFirstGameState(Realm realm) {
        Pack pack = realm.where(Pack.class).findFirst();
        pack.setState(PACK_STATE_CURRENT);
        Level level = realm.where(Level.class).findFirst();
        level.setState(LEVEL_STATE_CURRENT);
    }

}
