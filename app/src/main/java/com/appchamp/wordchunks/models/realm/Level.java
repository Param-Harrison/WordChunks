package com.appchamp.wordchunks.models.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Level extends RealmObject {

    @PrimaryKey
    private String id;
    private String clue;
    private int state;  // 0 = locked, 1 = current, 2 = finished
    private RealmList<Word> words;
    private RealmList<Chunk> chunks;
    private RealmList<Chunk> input;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public RealmList<Chunk> getInput() {
        return input;
    }

    public void setInput(RealmList<Chunk> input) {
        this.input = input;
    }
}
