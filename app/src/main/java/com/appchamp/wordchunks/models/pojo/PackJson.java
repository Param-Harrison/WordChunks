package com.appchamp.wordchunks.models.pojo;

import java.util.List;


public class PackJson {

    private String title;

    private String color;

    private List<LevelJson> levels;

    public PackJson() {
        // no-args constructor
    }

    public String getTitle() {
        return title;
    }

    public String getColor() {
        return color;
    }

    public List<LevelJson> getLevels() {
        return levels;
    }

}
