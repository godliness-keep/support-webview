package com.longrise.android.x5web.internal;


import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.longrise.android.x5web.internal.webcallback.IWebLoadListener;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by godliness on 2020/9/4.
 *
 * @author godliness
 */
final class BridgeDelegate implements OnBridgeListener{

    private static final int STATE_LOAD_FAILED = 0;
    private static final int STATE_LOAD_COMPLETED = 1;
    private int mCurrentLoadStatus = -1;

    private static final String DOC_CALLBACK = "javaScript:function onPageLoaded(){" +
            "if(typeof document.URL){" +
            "if(document.URL.indexOf(\"data:text/html\") < 0){" +
            "lrBridge.onPageLoaded();" +
            "}" +
            "}" +
            "}" +
            "javaScript:onPageLoaded();";

    private IWebLoadListener mLoadCallback;
    private String mCurrentUrl;
    private boolean mPageLoaded;

    @Override
    public boolean beforeUrlLoading(String url) {
        if (mLoadCallback != null) {
            return mLoadCallback.shouldOverrideUrlLoading(url);
        }
        return false;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (mLoadCallback != null) {
            mLoadCallback.onProgressChanged(newProgress);
        }

        onPageLoading(view, newProgress);
    }

    @Override
    public void onPageLoaded(@Nullable WebView view) {
        this.mPageLoaded = true;

        if (view != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    notifyLoadCompleted();
                }
            });
        }
    }

    @Override
    public void onReceivedTitle(String title) {
        if (mLoadCallback != null) {
            mLoadCallback.onReceivedTitle(title);
        }
    }

    @Override
    public void onPageStarted() {
    }

    @Override
    public void onPageFinished() {
    }

    @Override
    public void onReceivedError() {
    }

    @Override
    public void registerCallback(IWebLoadListener webCallback) {
        this.mLoadCallback = webCallback;
    }

    @Override
    public void destroy() {
        mLoadCallback = null;
        mCurrentLoadStatus = -1;
    }

    static OnBridgeListener getInstance() {
        return new BridgeDelegate();
    }

    private String getPageUrl(WebView view) {
        final String url = view.getUrl();
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        final int index = url.indexOf("?");
        if (index > 0) {
            return url.substring(0, index);
        }
        return url;
    }

    private void onPageLoading(WebView view, int curProgress) {
        final String url = getPageUrl(view);

        if (!TextUtils.equals(url, mCurrentUrl)) {
            if (curProgress >= 75) {
                this.mCurrentUrl = url;
                this.mPageLoaded = false;
                view.loadUrl(DOC_CALLBACK);
            }
        } else if (curProgress >= 100) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    if (mPageLoaded) {
                        notifyLoadCompleted();
                    } else {
                        notifyLoadFailed();
                        mCurrentUrl = null;
                    }
                }
            });
        }
    }

    private void notifyLoadFailed() {
        if (mCurrentLoadStatus == STATE_LOAD_FAILED) {
            return;
        }
        if (mLoadCallback != null) {
            mLoadCallback.loadFailed();
        }
        this.mCurrentLoadStatus = STATE_LOAD_FAILED;
    }

    private void notifyLoadCompleted() {
        if (mCurrentLoadStatus == STATE_LOAD_COMPLETED) {
            return;
        }
        if (mLoadCallback != null) {
            mLoadCallback.loadSucceed();
        }
        this.mCurrentLoadStatus = STATE_LOAD_COMPLETED;
    }

    private BridgeDelegate() {

    }
}
