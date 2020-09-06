package com.longrise.android.jssdk.sender.base;

import android.support.annotation.NonNull;
import android.webkit.WebView;

/**
 * Created by godliness on 2020-04-21.
 *
 * @author godliness
 */
public abstract class SenderAgent<T extends ICallback> {

    public abstract SenderAgent<T> callback(T callback);

    public abstract void to(@NonNull WebView target);

    abstract int getId();

    abstract T getCallback();
}
