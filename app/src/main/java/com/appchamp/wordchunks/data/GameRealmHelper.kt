package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.util.Constants.STATE_CURRENT
import io.realm.Realm


object GameRealmHelper {

    /**
     * Initializes the game state in the beginning of the game.
     */
    fun initFirstGameState(realm: Realm) {
        val pack = realm.where(Pack::class.java).findFirst()
        pack.state = STATE_CURRENT
        val level = realm.where(Level::class.java).findFirst()
        level.state = STATE_CURRENT
    }

}
