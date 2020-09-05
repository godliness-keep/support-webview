package com.longrise.android.jssdk.sender;

import android.support.annotation.NonNull;
import android.webkit.WebView;

/**
 * Created by godliness on 2020-04-29.
 *
 * @author godliness
 */
public interface INativeListener<T> {

    /**
     * 状态 {@link com.longrise.android.jssdk.Response#RESULT_OK}
     */
    INativeListener<T> state(int state);

    /**
     * 说明
     */
    INativeListener<T> desc(String desc);

    /**
     * 内容
     */
    INativeListener<T> result(T t);

    /**
     * 通知目标
     */
    void notify(@NonNull WebView target);
}
