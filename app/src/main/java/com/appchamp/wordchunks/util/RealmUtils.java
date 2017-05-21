package com.appchamp.wordchunks.util;

import java.util.Random;
import java.util.UUID;


public final class RealmUtils {

    private RealmUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Returns a randomly generated id used with Realm objects, it is basically for the navigation
     * purposes between activities.
     *
     * @return the random id string.
     */
    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Returns a randomly shuffled array of ints, implements Fisher-Yates shuffle.
     *
     * @param chunksSize the size of the array.
     * @return a randomly shuffled array of ints.
     */
    public static int[] shuffleArray(int chunksSize) {
        int[] array = new int[chunksSize];
        for (int i = 0; i < chunksSize; i++) {
            array[i] = i;
        }
        Random random = new Random();
        for (int i = chunksSize - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            // Simple swap;
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
        return array;
    }
}
