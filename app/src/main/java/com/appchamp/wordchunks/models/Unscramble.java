package com.appchamp.wordchunks.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Unscramble extends RealmObject {

    @PrimaryKey
    private long id;

    private Eng eng;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Eng getEng() {
        return eng;
    }

    public void setEng(Eng eng) {
        this.eng = eng;
    }

}