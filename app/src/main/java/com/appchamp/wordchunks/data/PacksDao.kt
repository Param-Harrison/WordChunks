package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.realmdb.models.pojo.PackJson
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import io.realm.Realm
import io.realm.RealmList
import java.util.*


/**
 * Packs
 */
object PacksDao {

    /**
     * Creates the list of Realm Pack objects using the PackJson list.
     *
     * @param realm
     * @param packJsonList the list of PackJson objects.
     */
    fun createPacks(realm: Realm, packJsonList: List<PackJson>) {
        val packs = RealmList<Pack>()
        for (i in packJsonList.indices) {
            val pack = realm.createObject(Pack::class.java, UUID.randomUUID().toString())
            val packPojo = packJsonList[i]
            pack.title = packPojo.title
            pack.color = packPojo.color
            LevelsDao.createLevels(realm, pack, packPojo.levels)
            packs.add(pack)
        }
    }
}
