package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.models.pojo.PackJson
import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE
import io.realm.Realm
import io.realm.RealmList
import java.util.*


/**
 * Packs
 */
object PacksRealmHelper {

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
            LevelsRealmHelper.createLevels(realm, pack, packPojo.levels)
            packs.add(pack)
        }
    }

    fun findAllPacks(realm: Realm): List<Pack> = realm
            .where(Pack::class.java)
            .findAll()

    fun findFirstPackByState(realm: Realm, state: Int): Pack = realm
            .where(Pack::class.java)
            .equalTo(REALM_FIELD_STATE, state)
            .findFirst()

    fun findFirstPackById(realm: Realm, id: String?): Pack = realm
            .where(Pack::class.java)
            .equalTo(REALM_FIELD_ID, id)
            .findFirst()
}
