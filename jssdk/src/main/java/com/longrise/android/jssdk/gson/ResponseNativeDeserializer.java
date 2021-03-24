package com.longrise.android.jssdk.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.longrise.android.jssdk.Response;
import com.longrise.android.jssdk.ResponseScript;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-16.
 *
 * @author godliness
 */
final class ResponseNativeDeserializer implements JsonDeserializer<ResponseScript<String>> {

    static Type getType() {
        return new TypeToken<ResponseScript<String>>() {
        }.getType();
    }

    static ResponseNativeDeserializer create() {
        return new ResponseNativeDeserializer();
    }

    @Override
    public ResponseScript<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement versionElement = jsonObject.get("version");
        final int version = versionElement != null ? versionElement.getAsInt() : -1;

        final JsonElement idElement = jsonObject.get("id");
        final int id = idElement != null ? idElement.getAsInt() : -1;

        final String result;
        final JsonElement jsonResult = jsonObject.get("result");
        if (jsonResult == null) {
            result = null;
        } else if (jsonResult.isJsonObject()) {
            result = jsonResult.getAsJsonObject().toString();
        } else if (jsonResult.isJsonArray()) {
            result = jsonResult.getAsJsonArray().toString();
        } else {
            result = jsonResult.getAsString();
        }
        final ResponseScript<String> response = Response.scriptResponse();
        response.setVersion(version);
        response.setCallbackId(id);
        response.deserialize(result);
        return response;
    }
}
