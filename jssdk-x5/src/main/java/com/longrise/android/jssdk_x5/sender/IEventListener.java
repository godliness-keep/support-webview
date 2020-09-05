package com.longrise.android.jssdk_x5.sender;

import android.support.annotation.NonNull;

import com.longrise.android.jssdk_x5.sender.base.ICallback;
import com.longrise.android.jssdk_x5.sender.base.SenderAgent;
import com.tencent.smtt.sdk.WebView;

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
