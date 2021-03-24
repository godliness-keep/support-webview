package com.longrise.android.jssdk_x5;



import android.support.annotation.NonNull;

import com.longrise.android.jssdk_x5.core.JsCallManager;
import com.longrise.android.jssdk_x5.sender.IMethodListener;
import com.longrise.android.jssdk_x5.sender.base.ICallback;
import com.longrise.android.jssdk_x5.sender.base.SenderAgent;
import com.longrise.android.jssdk_x5.sender.base.SenderImpl;
import com.tencent.smtt.sdk.WebView;

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
