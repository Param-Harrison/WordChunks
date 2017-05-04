package com.appchamp.wordchunks.models.pojo;

import java.util.List;


public class PackPojo {

    private String title;

    private List<LevelPojo> levels;

    public PackPojo() {
        // no-args constructor
    }

    public String getTitle() {
        return title;
    }

    public List<LevelPojo> getLevels() {
        return levels;
    }
}
