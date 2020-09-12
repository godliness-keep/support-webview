package com.longrise.android.x5web.internal;


import com.longrise.android.x5web.internal.webcallback.IWebLoadListener;

/**
 * Created by godliness on 2020/9/7.
 *
 * @author godliness
 */
public interface OnBridgeListener {

    boolean beforeUrlLoading(String url);

    void onProgressChanged(int newProgress);

    void onReceivedTitle(String title);

    void onPageStarted();

    void onPageFinished();

    void onReceivedError();

    void registerCallback(IWebLoadListener webCallback);

    void destroy();
}
