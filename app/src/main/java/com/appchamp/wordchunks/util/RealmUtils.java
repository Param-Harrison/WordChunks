package com.appchamp.wordchunks.util;

import java.util.UUID;


public class RealmUtils {

    /**
     * Returns a randomly generated id used with Realm objects, it is basically for the navigation
     * purposes between activities.
     * @return the random id string.
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }
}
