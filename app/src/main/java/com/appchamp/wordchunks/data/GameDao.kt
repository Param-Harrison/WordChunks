package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.models.realm.LevelState
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.realmdb.models.realm.PackState
import io.realm.Realm


object GameDao {

    /**
     * Initializes the game state in the beginning of the game.
     */
    fun initFirstGameState(realm: Realm) {
        val pack = realm.where(Pack::class.java).findFirst()
        pack.state = PackState.IN_PROGRESS.value
        val level = realm.where(Level::class.java).findFirst()
        level.state = LevelState.IN_PROGRESS.value
    }

}
