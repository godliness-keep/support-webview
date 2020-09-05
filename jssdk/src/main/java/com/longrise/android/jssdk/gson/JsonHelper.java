package com.longrise.android.jssdk.gson;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.longrise.android.jssdk.Request;
import com.longrise.android.jssdk.core.protocol.Result;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-09.
 *
 * @author godliness
 */
public final class JsonHelper {

    private static final Gson GSON;

    static {
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Request.class, StringRequestDeserializer.create());
        builder.registerTypeAdapter(ResponseNativeDeserializer.getType(), ResponseNativeDeserializer.create());
        builder.registerTypeAdapter(Result.class, ResultDeserializer.create());
        GSON = builder.serializeNulls().disableHtmlEscaping().excludeFieldsWithoutExposeAnnotation().create();
    }

    public static Gson getGson() {
        return GSON;
    }

    public static String toJson(Object json) {
        return GSON.toJson(json);
    }

    @Nullable
    public static <T> T fromJson(String jsonStr, Type type) {
        return GSON.fromJson(jsonStr, type);
    }

    public static <T> T fromJson(Reader reader, Type type) {
        return GSON.fromJson(reader, type);
    }
}
