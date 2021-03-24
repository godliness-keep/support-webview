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
public interface IMethodListener<P, T extends ICallback> {

    /**
     * 携带参数
     */
    SenderAgent<T> params(P params);

    /**
     * Call to target
     */
    void to(@NonNull WebView target);
}
