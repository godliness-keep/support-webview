package com.longrise.android.x5web.internal;


import android.os.Handler;

import com.longrise.android.x5web.internal.bridge.BaseDownloader;
import com.longrise.android.x5web.internal.bridge.BaseWebBridge;
import com.longrise.android.x5web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.x5web.internal.bridge.BaseWebViewClient;


/**
 * Created by godliness on 2020/9/11.
 *
 * @author godliness
 */
public interface IBridgeAgent<T extends IBridgeAgent<T>> {

    Handler getHandler();

    boolean isFinishing();

    BaseWebBridge<T> getBridge();

    BaseWebViewClient<T> getWebViewClient();

    BaseWebChromeClient<T> getWebChromeClient();

    BaseDownloader<T> getDownloadHelper();
}
