package com.longrise.android.jssdk.sender;

import android.support.annotation.NonNull;
import android.webkit.WebView;

import com.longrise.android.jssdk.sender.base.ICallback;
import com.longrise.android.jssdk.sender.base.SenderAgent;

/**
 * Created by godliness on 2020-04-26.
 *
 * @author godliness
 */
public interface IEventListener<P, T extends ICallback> {

    /**
     * 携带参数
     */
    IEventListener<P, T> params(P params);

    /**
     * 返回值
     */
    SenderAgent<T> callback(T callback);

    /**
     * Call to target
     */
    void to(@NonNull WebView target);
}
