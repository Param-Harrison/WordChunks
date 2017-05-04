package com.appchamp.wordchunks.models.realm;


import io.realm.RealmList;
import io.realm.RealmObject;

public class Word extends RealmObject {

    private String word;
    private int state;  // 0 = not solved, 1 = solved
    private RealmList<Chunk> chunks;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public RealmList<Chunk> getChunks() {
        return chunks;
    }

    public void setChunks(RealmList<Chunk> chunks) {
        this.chunks = chunks;
    }
}