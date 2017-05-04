package com.appchamp.wordchunks.models.realm;


import io.realm.RealmList;
import io.realm.RealmObject;


public class DailyPuzzle extends RealmObject {

    private String clue;
    private boolean solved;
    private RealmList<Word> words;
    private RealmList<Chunk> chunks;

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
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
