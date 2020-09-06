package com.longrise.android.jssdk;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.longrise.android.jssdk.core.protocol.Result;
import com.longrise.android.jssdk.sender.INativeListener;
import com.longrise.android.jssdk.stream.StreamSender;

/**
 * Created by godliness on 2020-04-29.
 *
 * @author godliness
 */
final class ResponseNative<T> extends Response implements INativeListener<T> {

    @Expose(deserialize = false)
    @SerializedName("result")
    private Result<T> result;

    static <T> INativeListener<T> createInternal(int id) {
        return new ResponseNative<>(id);
    }

    @Override
    public INativeListener<T> state(int state) {
        createResultIfNeed().setState(state);
        return this;
    }

    @Override
    public INativeListener<T> desc(String desc) {
        createResultIfNeed().setDesc(desc);
        return this;
    }

    @Override
    public INativeListener<T> result(T t) {
        createResultIfNeed().setResult(t);
        return this;
    }

    @Override
    public void notify(@NonNull WebView target) {
        StreamSender.sendStream(this, target);
    }

    private Result<T> createResultIfNeed() {
        if (result == null) {
            result = new Result<>();
        }
        return result;
    }

    private ResponseNative(int id) {
        setCallbackId(id);
    }
}
