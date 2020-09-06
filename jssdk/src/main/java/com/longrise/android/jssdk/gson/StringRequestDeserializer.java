package com.longrise.android.jssdk.gson;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.longrise.android.jssdk.Request;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-27.
 *
 * @author godliness
 */
final class StringRequestDeserializer extends BaseRequestDeserializer<String> {

    static Type getType() {
        return new TypeToken<Request<String>>() {
        }.getType();
    }

    static StringRequestDeserializer create() {
        return new StringRequestDeserializer();
    }

    @Override
    boolean interceptParseParams(Class<?> clz) {
        return String.class.isAssignableFrom(clz);
    }

    @Override
    String parseParams(JsonElement params) {
        if (params == null || params.isJsonNull()) {
            return "";
        } else if (params.isJsonObject()) {
            return params.getAsJsonObject().toString();
        } else if (params.isJsonArray()) {
            return params.getAsJsonArray().toString();
        } else {
            return params.getAsString();
        }
    }
}
