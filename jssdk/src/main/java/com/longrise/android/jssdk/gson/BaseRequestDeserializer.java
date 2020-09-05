package com.longrise.android.jssdk.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.longrise.android.jssdk.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-27.
 *
 * @author godliness
 */
abstract class BaseRequestDeserializer<T> implements JsonDeserializer<Request> {

    @Override
    public Request deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final Request<Object> request = new Request<>();

        final JsonObject jsonObject = json.getAsJsonObject();

        final JsonElement versionElement = jsonObject.get("version");
        request.setVersion(versionElement != null ? versionElement.getAsInt() : -1);

        final JsonElement idElement = jsonObject.get("id");
        request.setCallbackId(idElement != null ? idElement.getAsInt() : -1);

        final JsonElement eventElement = jsonObject.get("eventName");
        request.setEventName(eventElement != null ? eventElement.getAsString() : null);

        final JsonElement paramsElement = jsonObject.get("params");
        request.setParams(getParams(paramsElement, getRealType(typeOfT)));
        return request;
    }

    abstract boolean interceptParseParams(Class<?> clz);

    abstract T parseParams(JsonElement params);

    private Object getParams(JsonElement paramsElement, Type realType) {
        if (interceptParseParams((Class<?>) realType)) {
            return parseParams(paramsElement);
        }

        if (paramsElement == null || paramsElement.isJsonNull()) {
            return null;
        } else if (paramsElement.isJsonObject()) {
            return JsonHelper.fromJson(paramsElement.getAsJsonObject().toString(), realType);
        } else if (paramsElement.isJsonArray()) {
            return JsonHelper.fromJson(paramsElement.getAsJsonArray().toString(), realType);
        } else if (paramsElement.isJsonPrimitive()) {
            final JsonPrimitive primitive = paramsElement.getAsJsonPrimitive();
            if (primitive.isString()) {
                return primitive.getAsString();
            } else if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else {
                final Number number = primitive.getAsNumber();
                final Class<?> clz = (Class<?>) realType;
                if (Integer.class.isAssignableFrom(clz)) {
                    return number.intValue();
                } else if (Long.class.isAssignableFrom(clz)) {
                    return number.longValue();
                } else if (Double.class.isAssignableFrom(clz)) {
                    return number.doubleValue();
                } else if (Float.class.isAssignableFrom(clz)) {
                    return number.floatValue();
                } else if (Short.class.isAssignableFrom(clz)) {
                    return number.shortValue();
                } else if (Byte.class.isAssignableFrom(clz)) {
                    return number.byteValue();
                }
            }
        }
        return null;
    }

    private Type getRealType(Type typeOfT) {
        if (typeOfT instanceof ParameterizedType) {
            return ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        }
        return null;
    }
}
