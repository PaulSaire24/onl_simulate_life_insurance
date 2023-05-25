package com.bbva.rbvd.lib.r301.impl.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonHelper {

    private static final String DATE = "yyyy-MM-dd";
    private static final JsonHelper INSTANCE = new JsonHelper();

    private final Gson gson;

    private JsonHelper() {
        gson = new GsonBuilder()
                .setDateFormat(DATE)
                .create();
    }

    public static JsonHelper getInstance() { return INSTANCE; }

    public String serialization(Object o) { return this.gson.toJson(o); }

    public <T> T fromString(String src, Class<T> clazz) { return this.gson.fromJson(src, clazz); }

    public <T> T deserialization(String src, Class<T> clazz) { return this.gson.fromJson(src, clazz); }
}
