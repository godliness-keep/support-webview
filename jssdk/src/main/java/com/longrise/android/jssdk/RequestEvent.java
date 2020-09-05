package com.longrise.android.jssdk;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.longrise.android.jssdk.core.JsCallManager;
import com.longrise.android.jssdk.sender.IEventListener;
import com.longrise.android.jssdk.sender.base.ICallback;
import com.longrise.android.jssdk.sender.base.SenderAgent;
import com.longrise.android.jssdk.sender.base.SenderImpl;

/**
 * Created by godliness on 2020-04-26.
 *
 * @author godliness
 */
final class RequestEvent<P, T extends ICallback> extends Request<P> implements IEventListener<P, T> {

    @SuppressWarnings("unchecked")
    static <P, T extends ICallback> IEventListener<P, T> create(String eventName) {
        return new RequestEvent<>().eventName(eventName);
    }

    @Override
    public IEventListener<P, T> params(P params) {
        setParams(params);
        return this;
    }

    @Override
    public SenderAgent<T> callback(T callback) {
        return new SenderImpl<T>(this).callback(callback);
    }

    @Override
    public void to(@NonNull WebView target) {
        JsCallManager.callJavaScriptEvent(target, this);
    }

    private IEventListener eventName(String eventName) {
        setEventName(eventName);
        return this;
    }

    private RequestEvent() {

    }
}
