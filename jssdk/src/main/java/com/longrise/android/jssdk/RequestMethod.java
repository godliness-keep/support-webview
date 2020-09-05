package com.longrise.android.jssdk;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.longrise.android.jssdk.core.JsCallManager;
import com.longrise.android.jssdk.sender.IMethodListener;
import com.longrise.android.jssdk.sender.base.ICallback;
import com.longrise.android.jssdk.sender.base.SenderAgent;
import com.longrise.android.jssdk.sender.base.SenderImpl;

/**
 * Created by godliness on 2020-04-26.
 *
 * @author godliness
 */
final class RequestMethod<P, T extends ICallback> extends Request<P> implements IMethodListener<P, T> {

    @SuppressWarnings("unchecked")
    static <P, T extends ICallback> IMethodListener<P, T> create(String javaScriptMethodName) {
        return (IMethodListener<P, T>) new RequestMethod<>().methodName(javaScriptMethodName);
    }

    @Override
    public SenderAgent<T> params(P params) {
        setParams(params);
        return new SenderImpl<>(this);
    }

    @Override
    public void to(@NonNull WebView target) {
        JsCallManager.callJavaScriptMethod(target, this);
    }

    private IMethodListener methodName(String methodName) {
        setMethodName(methodName);
        return this;
    }

    private RequestMethod() {

    }
}
