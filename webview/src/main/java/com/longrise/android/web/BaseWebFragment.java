package com.longrise.android.web;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;

import com.longrise.android.web.internal.BaseWebView;
import com.longrise.android.web.internal.IBridgeAgent;
import com.longrise.android.web.internal.IWebLoadListener;
import com.longrise.android.web.internal.Internal;
import com.longrise.android.web.internal.bridge.BaseDownloader;
import com.longrise.android.web.internal.bridge.BaseWebBridge;
import com.longrise.android.web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.web.internal.bridge.BaseWebViewClient;

/**
 * Created by godliness on 2020/9/11.
 *
 * @author godliness
 */
public abstract class BaseWebFragment<T extends BaseWebFragment<T>> extends Fragment implements
        IWebLoadListener, Handler.Callback, IBridgeAgent<T> {

    private final Handler mHandler = new Handler(this);
    private BaseWebView mWebView;

    /**
     * Returns the current layout resource id
     *
     * @param state {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @return The current layout id
     */
    protected abstract int getLayoutResourceId(@Nullable Bundle state);

    /**
     * Here the {@link #findViewById(int)}
     */
    protected abstract void initView();

    /**
     * Register event or unregister
     *
     * @param regEvent Current state
     */
    protected abstract void regEvent(boolean regEvent);

    /**
     * Returns the current WebView instance
     *
     * @return {@link BaseWebView}
     */
    public abstract BaseWebView getWebView();

    /**
     * Perform load Web address in {@link #initView()}
     */
    protected void loadUrl(String url) {
        post(loadTask(url));
    }

    /**
     * 拦截通过 Handler 发送的任务
     */
    protected boolean onHandleMessage(Message msg) {
        return false;
    }

    /**
     * 扩展 Bridge {@link BaseWebBridge}
     */
    public BaseWebBridge<T> getBridge() {
        return null;
    }

    /**
     * 扩展 WebViewClient {@link BaseWebViewClient}
     */
    public BaseWebViewClient<T> getWebViewClient() {
        return null;
    }

    /**
     * 扩展 WebChromeClient {@link BaseWebChromeClient}
     */
    public BaseWebChromeClient<T> getWebChromeClient() {
        return null;
    }

    /**
     * 扩展 WebView 下载 {@link WebView#setDownloadListener(DownloadListener)}
     */
    public BaseDownloader<T> getDownloadHelper() {
        return null;
    }

    /**
     * 用于拦截 URL 加载 {@link BaseWebViewClient#shouldOverrideUrlLoading(WebView, String)}
     *
     * @return true 表示拦截本次加载
     */
    @Override
    public boolean shouldOverrideUrlLoading(String url) {
        return false;
    }

    /**
     * 通知 WebView 回退
     *
     * @return true 表示由 WebView 消费，否则开发人员决定
     */
    protected boolean webViewCanGoBack() {
        if (mWebView != null) {
            return mWebView.webViewGoBack();
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.recycle();
            mWebView = null;
        }
        regEvent(false);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * 获取 Handler 实例
     */
    public final Handler getHandler() {
        return mHandler;
    }

    /**
     * 通过 Handler 发送任务
     */
    protected final void post(Runnable task) {
        postDelayed(task, 0);
    }

    /**
     * 通过 Handler 发送 Delay 任务
     */
    protected final void postDelayed(Runnable task, int delayMillis) {
        mHandler.postDelayed(task, delayMillis);
    }

    protected final <V extends View> V findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    /**
     * Notify the WebView to reload
     */
    protected final void notifyWebViewReload() {
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    @Override
    public final boolean isFinishing() {
        return isDetached();
    }

    @Override
    public final boolean handleMessage(Message msg) {
        if (isFinishing()) {
            return true;
        }
        if (onHandleMessage(msg)) {
            return true;
        }
        return onBridgeHandleMessage(msg);
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResourceId(savedInstanceState), container, false);
    }

    @Override
    public final void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createWebFrame();
        regEvent(true);
    }

    @SuppressWarnings("unchecked")
    private void createWebFrame() {
        final BaseWebView webView = getWebView();
        if (webView == null) {
            throw new NullPointerException("getWebView() == null");
        }
        final Internal<?> internal = new Internal<>((T) this, webView);
        internal.install();
        this.mWebView = webView;
    }

    private boolean onBridgeHandleMessage(Message msg) {
        return mWebView != null && mWebView.onHandleMessage(msg);
    }

    private Runnable loadTask(final String url) {
        return new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(url);
            }
        };
    }
}
