package com.appchamp.wordchunks.util;


import android.content.res.AssetManager;

import com.appchamp.wordchunks.models.pojo.PackJson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.appchamp.wordchunks.util.Constants.JSON_FILE_NAME;

public final class JsonUtils {

    private JsonUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Returns a list of PackJson objects deserialized using the Gson library from the json
     * string.
     *
     * @param assetManager used to access assets where json file is located.
     * @return the list of PackJson objects.
     */
    public static List<PackJson> getPacksListFromJson(AssetManager assetManager) {
        Type listType = new TypeToken<ArrayList<PackJson>>() {
        }.getType();
        String json = FileUtils.getFileFromAsset(assetManager, JSON_FILE_NAME);
        return new GsonBuilder()
                .create()
                .fromJson(json, listType);
    }


}
