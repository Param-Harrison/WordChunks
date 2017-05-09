package com.appchamp.wordchunks.models.realm;

import io.realm.RealmObject;
import io.realm.annotations.Index;


public class Chunk extends RealmObject {

    @Index
    private String levelId;
    @Index
    private String wordId;
    @Index
    private String chunk;
    @Index
    private long state;
    @Index
    private int position;

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
