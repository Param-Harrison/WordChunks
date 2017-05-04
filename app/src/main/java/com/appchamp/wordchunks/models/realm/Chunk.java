package com.appchamp.wordchunks.models.realm;

import io.realm.RealmObject;


public class Chunk extends RealmObject {

    private String chunk;
    private int state;
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
