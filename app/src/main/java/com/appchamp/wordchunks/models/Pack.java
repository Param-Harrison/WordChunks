package com.appchamp.wordchunks.models;

import io.realm.RealmList;
import io.realm.RealmObject;


public class Pack extends RealmObject {

    private long id;

    private String name;

    private RealmList<Level> levels = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<Level> getLevels() {
        return levels;
    }

    public void setLevels(RealmList<Level> levels) {
        this.levels = levels;
    }

}