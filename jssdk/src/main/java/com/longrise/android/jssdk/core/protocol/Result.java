package com.longrise.android.jssdk.core.protocol;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.longrise.android.jssdk.Response;
import com.longrise.android.jssdk.gson.JsonHelper;
import com.longrise.android.jssdk.gson.ParameterizedTypeImpl;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-13.
 *
 * @author godliness
 */
public final class Result<T> {

    @Expose
    @SerializedName("state")
    private int state = Response.RESULT_OK;
    @Expose
    @SerializedName("desc")
    private String desc = "ok";
    @Expose
    @SerializedName("result")
    private T result;

    public static Result<String> parseResult(String json) {
        return parseResult(json, String.class);
    }

    public static <T> Result<T> parseResult(String json, Class<T> clzOfT) {
        return JsonHelper.fromJson(json, ParameterizedTypeImpl.getTypeImpl(Result.class, clzOfT));
    }

    public static <T> Result<T> parseResult(String json, Type typeOfT) {
        return JsonHelper.fromJson(json, ParameterizedTypeImpl.getTypeImpl(Result.class, typeOfT));
    }

    public Result() {

    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public T getResult() {
        return result;
    }

    public final String toJson() {
        return JsonHelper.toJson(this);
    }

    @Override
    public String toString() {
        return "Result{" +
                "state=" + state +
                ", desc='" + desc + '\'' +
                ", result=" + result +
                '}';
    }
}
