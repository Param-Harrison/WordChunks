package com.appchamp.wordchunks.models;

import io.realm.RealmList;
import io.realm.RealmObject;


public class Level extends RealmObject {

    private String clue;

    private RealmList<Word> words = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public RealmList<Word> getWords() {
        return words;
    }

    public void setWords(RealmList<Word> words) {
        this.words = words;
    }

}