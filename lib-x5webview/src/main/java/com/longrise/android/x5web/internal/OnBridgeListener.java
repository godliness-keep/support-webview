package com.longrise.android.x5web.internal;


import android.support.annotation.Nullable;

import com.longrise.android.x5web.internal.webcallback.IWebLoadListener;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by godliness on 2020/9/7.
 *
 * @author godliness
 */
public interface OnBridgeListener {

    boolean beforeUrlLoading(String url);

    void onProgressChanged(WebView view, int newProgress);

    void onReceivedTitle(String title);

    void onPageStarted();

    void onPageFinished();

    void onPageLoaded(@Nullable WebView view);

    void onReceivedError();

    void registerCallback(IWebLoadListener webCallback);

    void destroy();
}
