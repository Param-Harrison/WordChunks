package com.appchamp.wordchunks.data;

import com.appchamp.wordchunks.models.pojo.PackJson;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.util.RealmUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE;

/**
 * Packs
 */
public final class PacksRealmHelper {

    private PacksRealmHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Creates the list of Realm Pack objects using the PackJson list.
     *
     * @param realm
     * @param packJsonList the list of PackJson objects.
     */
    public static void createPacks(Realm realm, final List<PackJson> packJsonList) {
        RealmList<Pack> packs = new RealmList<>();
        for (int i = 0; i < packJsonList.size(); i++) {
            Pack pack = realm.createObject(Pack.class, RealmUtils.getUUID());
            PackJson packPojo = packJsonList.get(i);
            pack.setTitle(packPojo.getTitle());
            LevelsRealmHelper.createLevels(realm, pack, packPojo.getLevels());
            packs.add(pack);
        }
    }

    public static List<Pack> findAllPacks(Realm realm) {
        return realm
                .where(Pack.class)
                .findAll();
    }

    public static Pack findFirstPackByState(Realm realm, int state) {
        return realm
                .where(Pack.class)
                .equalTo(REALM_FIELD_STATE, state)
                .findFirst();
    }

    public static Pack findFirstPackById(Realm realm, String id) {
        return realm
                .where(Pack.class)
                .equalTo(REALM_FIELD_ID, id)
                .findFirst();
    }
}
