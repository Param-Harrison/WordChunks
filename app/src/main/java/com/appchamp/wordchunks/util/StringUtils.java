package com.appchamp.wordchunks.util;

import java.util.regex.Pattern;


public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static String[] getSplits(String input, String regex) {
        if (input == null) return null;
        return input.split(regex);
    }

    public static String getReplaceAll(String input, String regex, String replacement) {
        if (input == null) return null;
        return Pattern
                .compile(regex)
                .matcher(input)
                .replaceAll(replacement);
    }
}
