package com.appchamp.wordchunks.models.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;


public class Level extends RealmObject {

    @PrimaryKey
    private String id;
    private String packId;
    @Index
    private String clue;
    @Index
    private int state;  // 0 = locked, 1 = current, 2 = finished
    private RealmList<Word> words;
    private RealmList<Chunk> chunks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPackId() {
        return packId;
    }

    public void setPackId(String packId) {
        this.packId = packId;
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public RealmList<Word> getWords() {
        return words;
    }

    public void setWords(RealmList<Word> words) {
        this.words = words;
    }

    public RealmList<Chunk> getChunks() {
        return chunks;
    }

    public void setChunks(RealmList<Chunk> chunks) {
        this.chunks = chunks;
    }

}
