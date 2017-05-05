package com.appchamp.wordchunks.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.Index;


public class Chunk extends RealmObject {

    @Index
    private String chunk;
    @Index
    private int state;
    @Index
    private int position;

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}
