package com.appchamp.wordchunks.models;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;


public class Word extends RealmObject {

    private String chunks;

    private String word;

    @Ignore
    private boolean solved;

    public String getChunks() {
        return chunks;
    }

    public void setChunks(String chunks) {
        this.chunks = chunks;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

}