package com.appchamp.wordchunks.util


object Constants {

    val JSON_FILE_NAME = "data.json"
    val WORDS_SEPARATOR = " "
    val CHUNKS_SEPARATOR = ","

    val WORD_CHUNKS_PREFS = "WORD_CHUNKS_PREFS"
    val PREFS_REALM_CREATE_OBJECTS = "PREFS_REALM_CREATE_OBJECTS"
    val PREFS_HOW_TO_PLAY = "PREFS_HOW_TO_PLAY"

    val EXTRA_PACK_ID = "EXTRA_PACK_ID"
    val LEVEL_ID_KEY = "LEVEL_ID_KEY"
    val COLOR_ID_KEY = "COLOR_ID_KEY"
    val FACT_ID_KEY = "FACT_ID_KEY"
    val LEFT_ID_KEY = "LEFT_ID_KEY"
    val CLUE_ID_KEY = "CLUE_ID_KEY"

    val REALM_FIELD_ID = "id"
    val REALM_FIELD_PACK_ID = "packId"
    val REALM_FIELD_LEVEL_ID = "levelId"
    val REALM_FIELD_STATE = "state"

    val WORDS_GRID_NUM = 2
    val CHUNKS_GRID_NUM = 4

    // States of chunks
    val CHUNK_STATE_NORMAL = 0
    val CHUNK_STATE_GONE = -1

    // States of words
    val WORD_STATE_NOT_SOLVED = 0
    val WORD_STATE_SOLVED = 1

    // States of packs and levels
    val STATE_LOCKED = 0
    val STATE_CURRENT = 1
    val STATE_SOLVED = 2
}
