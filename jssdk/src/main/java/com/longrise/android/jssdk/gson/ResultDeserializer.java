package com.longrise.android.jssdk.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.longrise.android.jssdk.core.protocol.Result;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-27.
 *
 * @author godliness
 */
final class ResultDeserializer implements JsonDeserializer<Result> {

    static ResultDeserializer create() {
        return new ResultDeserializer();
    }

    @Override
    public Result deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final Result<Object> resultBody = new Result<>();

        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement stateElement = jsonObject.get("state");
        resultBody.setState(stateElement != null ? stateElement.getAsInt() : 0);

        final JsonElement descElement = jsonObject.get("desc");
        resultBody.setDesc(descElement != null ? descElement.getAsString() : "");

        final JsonElement resultElement = jsonObject.get("result");
        if (resultElement == null || resultElement.isJsonNull()) {
            resultBody.setResult(null);
        } else if (resultElement.isJsonObject()) {
            resultBody.setResult(JsonHelper.fromJson(resultElement.getAsJsonObject().toString(), getTypeOfT(typeOfT)));
        } else if (resultElement.isJsonArray()) {
            resultBody.setResult(JsonHelper.fromJson(resultElement.getAsJsonArray().toString(), getTypeOfT(typeOfT)));
        } else if (resultElement.isJsonPrimitive()) {
            final JsonPrimitive primitive = resultElement.getAsJsonPrimitive();
            if (primitive.isString()) {
                resultBody.setResult(primitive.getAsString());
            } else if (primitive.isBoolean()) {
                resultBody.setResult(primitive.getAsBoolean());
            } else {
                final Number number = resultElement.getAsNumber();
                Class<?> clz = (Class<?>) getTypeOfT(typeOfT);
                if (Integer.class.isAssignableFrom(clz)) {
                    resultBody.setResult(number.intValue());
                } else if (Long.class.isAssignableFrom(clz)) {
                    resultBody.setResult(number.longValue());
                } else if (Double.class.isAssignableFrom(clz)) {
                    resultBody.setResult(number.doubleValue());
                } else if (Float.class.isAssignableFrom(clz)) {
                    resultBody.setResult(number.floatValue());
                } else if (Short.class.isAssignableFrom(clz)) {
                    resultBody.setResult(number.shortValue());
                } else if (Byte.class.isAssignableFrom(clz)) {
                    resultBody.setResult(number.byteValue());
                }
            }
        }
        return resultBody;
    }

    private Type getTypeOfT(Type typeOfT) {
        if (typeOfT instanceof ParameterizedType) {
            return ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        }
        return null;
    }

    private ResultDeserializer() {

    }
}
