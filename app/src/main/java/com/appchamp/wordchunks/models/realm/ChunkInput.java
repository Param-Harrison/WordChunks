package com.appchamp.wordchunks.models.realm;


import io.realm.RealmObject;

public class ChunkInput extends RealmObject {

    private String levelId;
    private String chunk;
    private int positionInGrid;

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public String getChunk() {
        return chunk;
    }

    public void setChunk(String chunk) {
        this.chunk = chunk;
    }

    public int getPositionInGrid() {
        return positionInGrid;
    }

    public void setPositionInGrid(int positionInGrid) {
        this.positionInGrid = positionInGrid;
    }
}
