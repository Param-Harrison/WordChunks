package com.appchamp.wordchunks.util;


import android.content.Context;

import com.appchamp.wordchunks.models.pojo.PackPojo;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.appchamp.wordchunks.util.Constants.CHARSET_NAME;
import static com.appchamp.wordchunks.util.Constants.JSON_FILE_NAME;

public class JsonUtils {

    /**
     * Returns a list of PackPojo objects deserialized using the Gson library from the json
     * string.
     *
     * @param context used to access assets where json file is located.
     * @return the list of PackPojo objects.
     */
    public static List<PackPojo> getPacksListFromJson(Context context) {
        Type listType = new TypeToken<ArrayList<PackPojo>>() {
        }.getType();
        return new GsonBuilder()
                .create()
                .fromJson(loadJSONFromAsset(context), listType);
    }

    /**
     * Returns a new String when reading the json file from the assets folder is done.
     *
     * @param context used to access assets where json file is located.
     * @return the json string.
     */
    private static String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open(JSON_FILE_NAME);
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
