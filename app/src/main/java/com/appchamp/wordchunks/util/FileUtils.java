package com.appchamp.wordchunks.util;


import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

import static com.appchamp.wordchunks.util.Constants.CHARSET_NAME;

public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Returns a new String when reading the json file from the assets folder is done.
     *
     * @param assetManager used to access assets where json file is located.
     * @return the json string.
     */
    public static String getFileFromAsset(AssetManager assetManager, String fileName) {
        String json;
        try {
            InputStream is = assetManager.open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, CHARSET_NAME);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}