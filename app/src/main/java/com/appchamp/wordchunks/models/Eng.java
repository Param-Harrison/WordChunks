package com.appchamp.wordchunks.models;

import io.realm.RealmList;
import io.realm.RealmObject;


public class Eng extends RealmObject {

    private RealmList<Pack> packs = null;

    public RealmList<Pack> getPacks() {
        return packs;
    }

    public void setPacks(RealmList<Pack> packs) {
        this.packs = packs;
    }

}