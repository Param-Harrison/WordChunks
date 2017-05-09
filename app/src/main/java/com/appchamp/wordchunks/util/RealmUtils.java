package com.appchamp.wordchunks.util;

import java.util.Random;
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

    /**
     * Does this:
     * 0 1      1 4
     * 2 3  =>  2 5
     * 4 5      3 6
     */
    public static String getWordNum(int i) {
        int newPos = 0;
        switch (i) {
            case 0:
                newPos = 1;
                break;
            case 1:
                newPos = 4;
                break;
            case 2:
                newPos = 2;
                break;
            case 3:
                newPos = 5;
                break;
            case 4:
                newPos = 3;
                break;
            case 5:
                newPos = 6;
                break;
        }
        return String.valueOf(newPos);
    }

    // Implements Fisher–Yates shuffle
    public static int[] shuffleArray() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        Random rnd = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
        return array;
    }
}
