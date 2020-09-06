package com.longrise.android.jssdk.sender.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import com.longrise.android.jssdk.Request;

/**
 * Created by godliness on 2020-04-28.
 *
 * @author godliness
 */
public final class SenderImpl<T extends ICallback> extends SenderAgent<T> {

    private final Request mRequest;

    @Nullable
    private SendersManager mManager;
    @Nullable
    private T mCallback;

    public SenderImpl(Request request) {
        this.mRequest = request;
    }

    @Override
    public SenderAgent<T> callback(T callback) {
        this.mCallback = callback;
        this.mManager = SendersManager.getManager();
        this.mRequest.setCallbackId(mManager.aliveId());
        return this;
    }

    @Override
    public void to(@NonNull WebView target) {
        registerSender(target);
        this.mRequest.to(target);
    }

    @Override
    int getId() {
        return mRequest.getCallbackId();
    }

    @Nullable
    @Override
    T getCallback() {
        return mCallback;
    }

    private void registerSender(WebView target) {
        if (mCallback != null && mManager != null) {
            mManager.registerSender(target, this);
        }
    }
}
