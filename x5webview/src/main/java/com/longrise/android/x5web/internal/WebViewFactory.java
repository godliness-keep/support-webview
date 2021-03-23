package com.longrise.android.x5web.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.longrise.android.x5web.X5;

import java.util.LinkedList;

/**
 * Created by godliness on 2019-07-10.
 *
 * @author godliness
 */
final class WebViewFactory {

    private static final String TAG = "WebViewFactory";

    private static final int MAX_CACHE_SIZE = 2;
    private static final LinkedList<X5WebView> WEB_VIEWS = new LinkedList<>();

    @Nullable
    static X5WebView findWebView(@NonNull Context context) {
        X5WebView webView = null;
        synchronized (WEB_VIEWS) {
            if (WEB_VIEWS.size() > 0) {
                webView = WEB_VIEWS.removeFirst();

                if (X5.isDebug()) {
                    final int size = webView.copyBackForwardList().getSize();
                    X5.debug(TAG, "back stack size: " + size + " current url: " + webView.getOriginalUrl());
                }
            }
        }
        if (webView == null) {
            try {
                return new X5WebView(context.getApplicationContext());
            } catch (Exception e) {
                // 部分可能会出现无 WebView 的情况
                X5.print(e);
            }
        }
        return webView;
    }

    static boolean recycle(X5WebView webView) {
        synchronized (WEB_VIEWS) {
            if (WEB_VIEWS.size() < MAX_CACHE_SIZE) {
                WEB_VIEWS.add(webView);
                return true;
            }
        }
        return false;
    }

    private WebViewFactory() {
        throw new InstantiationError("WebViewFactory Cannot be initialized");
    }
}
