package com.appchamp.wordchunks.util;


public final class Constants {

    private Constants() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static final String JSON_FILE_NAME = "data.json";
    public static final String CHARSET_NAME = "UTF-8";
    public static final String WORDS_SEPARATOR = " ";
    public static final String CHUNKS_SEPARATOR = ",";

    public static final String WORD_CHUNKS_PREFS = "WORD_CHUNKS_PREFS";
    public static final String PREFS_REALM_CREATE_OBJECTS = "PREFS_REALM_CREATE_OBJECTS";
    public static final String PREFS_HOW_TO_PLAY = "PREFS_HOW_TO_PLAY";

    public static final String EXTRA_PACK_ID = "EXTRA_PACK_ID";
    public static final String EXTRA_LEVEL_ID = "EXTRA_LEVEL_ID";

    public static final String REALM_FIELD_ID = "id";
    public static final String REALM_FIELD_PACK_ID = "packId";
    public static final String REALM_FIELD_LEVEL_ID = "levelId";
    public static final String REALM_FIELD_STATE = "state";

    public static final int WORDS_GRID_NUM = 2;
    public static final int CHUNKS_GRID_NUM = 4;

    // States of chunks
    public static final int CHUNK_STATE_NORMAL = 0;
    public static final int CHUNK_STATE_GONE = -1;

    // States of words
    public static final int WORD_STATE_NOT_SOLVED = 0;
    public static final int WORD_STATE_SOLVED = 1;

    // States of packs
    public static final int PACK_STATE_LOCKED = 0;
    public static final int PACK_STATE_CURRENT = 1;
    public static final int PACK_STATE_SOLVED = 2;

    // States of levels
    public static final int LEVEL_STATE_LOCKED = 0;
    public static final int LEVEL_STATE_CURRENT = 1;
    public static final int LEVEL_STATE_SOLVED = 2;
}
