package com.longrise.android.web.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.longrise.android.mvp.BuildConfig;
import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;

import java.util.LinkedList;

/**
 * Created by godliness on 2019-07-10.
 *
 * @author godliness
 */
@SuppressWarnings("unchecked")
final class WebViewFactory {

    private static final String TAG = "WebViewFactory";

    private static final int MAX_CACHE_SIZE = 2;
    private static final LinkedList<BaseWebView> WEB_VIEWS = new LinkedList<>();

    @Nullable
    static <V extends BaseView, P extends BasePresenter<V>> BaseWebView<V, P> findWebView(@NonNull Context context) {
        BaseWebView<V, P> webView = null;
        synchronized (WEB_VIEWS) {
            if (WEB_VIEWS.size() > 0) {
                webView = WEB_VIEWS.removeFirst();

                if (BuildConfig.DEBUG) {
                    final int size = webView.copyBackForwardList().getSize();
                    Log.d(TAG, "back stack size: " + size + " current url: " + webView.getOriginalUrl());
                }
            }
        }
        if (webView == null) {
            try {
                return new BaseWebView<>(context.getApplicationContext());
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    // 部分可能会出现无 WebView 的情况
                    e.printStackTrace();
                }
            }
        }
        return webView;
    }

    static <V extends BaseView, P extends BasePresenter<V>> boolean recycle(BaseWebView<V, P> webView) {
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
