package com.longrise.android.jssdk;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.longrise.android.jssdk.core.protocol.base.AbsDataProtocol;
import com.longrise.android.jssdk.gson.JsonHelper;
import com.longrise.android.jssdk.gson.ParameterizedTypeImpl;
import com.longrise.android.jssdk.sender.IEventListener;
import com.longrise.android.jssdk.sender.IMethodListener;
import com.longrise.android.jssdk.sender.base.ICallback;

/**
 * Created by godliness on 2020-04-13.
 *
 * @author godliness
 */
public class Request<T> extends AbsDataProtocol {

    @Expose
    @SerializedName("params")
    private T params;

    /**
     * 调用 JavaScript 方法
     *
     * @param javaScriptMethodName method name in the JavaScript
     */
    public static <P, T extends ICallback> IMethodListener<P, T> call(String javaScriptMethodName) {
        return RequestMethod.create(javaScriptMethodName);
    }

    /**
     * 调用 JavaScript 事件
     *
     * @param eventName event name in the JavaScript
     */
    public static <P, T extends ICallback> IEventListener<P, T> callEvent(String eventName) {
        return RequestEvent.create(eventName);
    }

    /**
     * 解析为 Request<String> 类型
     *
     * @param json JSON format of string
     */
    public static Request<String> parseRequest(String json) {
        return parseRequest(json, String.class);
    }

    /**
     * 解析为 Request<T> 类型
     *
     * @param clz T.class
     */
    public static <T> Request<T> parseRequest(String json, Class<T> clz) {
        return JsonHelper.fromJson(json, ParameterizedTypeImpl.getTypeImpl(Request.class, clz));
    }

    /**
     * 解析为 Request<T>
     *
     * @param token TypeToken<T>(){}
     */
    public static <T> Request<T> parseRequest(String json, TypeToken<T> token) {
        return JsonHelper.fromJson(json, ParameterizedTypeImpl.getTypeImpl(Request.class, token.getType()));
    }

    public void to(@NonNull WebView target) {
        // do nothing in here
    }

    public void setParams(T params) {
        this.params = params;
    }

    public T getParams() {
        return params;
    }

    public Request() {
    }
}
