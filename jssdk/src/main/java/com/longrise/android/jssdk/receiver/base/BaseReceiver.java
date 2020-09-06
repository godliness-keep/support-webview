package com.longrise.android.jssdk.receiver.base;


import android.webkit.WebView;

import com.longrise.android.jssdk.BuildConfig;
import com.longrise.android.jssdk.Request;
import com.longrise.android.jssdk.gson.GenericHelper;
import com.longrise.android.jssdk.gson.JsonHelper;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-15.
 *
 * @author godliness
 */
public abstract class BaseReceiver<P> {

    private int id;
    private int version;
    private WeakReference<WebView> mTarget;
    private Type mType;

    public final ReceiverAgent alive() {
        return new ReceiverImpl<>(getEventName(getEventParamsType()), this);
    }

    protected abstract void onReceive(String params);

    protected abstract Class<P>[] getEventParamsType();

    protected final P parseParams(String json) {
        return JsonHelper.fromJson(json, getGenericType());
    }

    protected final int getId() {
        return id;
    }

    protected final int getVersion() {
        return version;
    }

    protected final WebView getTarget() {
        return mTarget.get();
    }

    protected final Type getGenericType() {
        return mType == null ? mType = GenericHelper.getTypeOfT(this, 0) : mType;
    }

    final void notifyReceiver(Request<String> request, WebView webView) {
        this.id = request.getCallbackId();
        this.version = request.getVersion();
        this.mTarget = new WeakReference<>(webView);
        onReceive(request.getParams());
    }

    private String getEventName(Class<P>[] params) {
        Method method;
        try {
            method = getClass().getDeclaredMethod("onEvent", params);
        } catch (NoSuchMethodException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return null;
        }
        return method.getAnnotation(EventName.class).value();
    }
}
