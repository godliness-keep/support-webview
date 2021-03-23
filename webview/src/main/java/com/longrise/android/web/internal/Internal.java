package com.longrise.android.web.internal;

import com.longrise.android.web.internal.bridge.BaseDownloader;
import com.longrise.android.web.internal.bridge.BaseWebBridge;
import com.longrise.android.web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.web.internal.bridge.BaseWebViewClient;

/**
 * Created by godliness on 2020/8/22.
 *
 * @author godliness
 */
public final class Internal<T extends IBridgeAgent<T>> {

    private final T mTarget;
    private BaseWebView mView;

    public Internal(T agent, BaseWebView view) {
        this.mTarget = agent;
        this.mView = view;

        if (agent instanceof IWebLoadListener) {
            view.registerCallback((IWebLoadListener) agent);
        }
    }

    public void install() {
        final BaseWebBridge<T> bridge = mTarget.getBridge();
        if (bridge != null) {
            bridge.attachTarget(mTarget, mView);
        } else {
            installBridge();
        }

        final BaseWebViewClient<T> webViewClient = mTarget.getWebViewClient();
        if (webViewClient != null) {
            webViewClient.attachTarget(mTarget, mView);
        } else {
            installWebViewClient();
        }

        final BaseWebChromeClient<T> webChromeClient = mTarget.getWebChromeClient();
        if (webChromeClient != null) {
            webChromeClient.attachTarget(mTarget, mView);
        } else {
            installWebChromeClient();
        }

        final BaseDownloader<T> downloader = mTarget.getDownloadHelper();
        if (downloader != null) {
            downloader.attachTarget(mTarget, mView);
        }
    }

    private void installBridge() {
        final BaseWebBridge<T> bridge = new BaseWebBridge<T>() {
        };
        bridge.attachTarget(mTarget, mView);
    }

    private void installWebViewClient() {
        final BaseWebViewClient<T> webViewClient = new BaseWebViewClient<T>() {
        };
        webViewClient.attachTarget(mTarget, mView);
    }

    private void installWebChromeClient() {
        final BaseWebChromeClient<T> webChromeClient = new BaseWebChromeClient<T>() {
        };
        webChromeClient.attachTarget(mTarget, mView);
    }
}
