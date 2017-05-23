package com.appchamp.wordchunks.models.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;


public class Pack extends RealmObject {

    @PrimaryKey
    private String id;// = UUID.randomUUID().toString();
    @Index
    private String title;
    @Index
    private String color;
    @Index
    private int state;  // 0 = locked, 1 = current, 2 = finished
    private RealmList<Level> levels;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public RealmList<Level> getLevels() {
        return levels;
    }

    public void setLevels(RealmList<Level> levels) {
        this.levels = levels;
    }

}
