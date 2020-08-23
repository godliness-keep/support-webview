package com.longrise.android.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;


import com.longrise.android.jssdk.core.bridge.BaseBridge;
import com.longrise.android.mvp.internal.BaseMvpActivity;
import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
import com.longrise.android.mvp.utils.MvpLog;
import com.longrise.android.web.internal.BaseWebView;
import com.longrise.android.web.internal.Internal;
import com.longrise.android.web.internal.SchemeConsts;
import com.longrise.android.web.internal.SettingInit;
import com.longrise.android.web.internal.bridge.BaseFileChooser;
import com.longrise.android.web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.web.internal.bridge.BaseWebViewClient;
import com.longrise.android.web.internal.webcallback.WebCallback;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
@SuppressWarnings("unused")
public abstract class BaseWebActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseMvpActivity<P> implements
        WebCallback.WebChromeListener, WebCallback.WebViewClientListener, Handler.Callback {

    private static final String TAG = "BaseWebActivity";

    private final Handler mHandler = new Handler(this);
    private BaseWebView mWebView;
    private BaseFileChooser<BaseWebActivity<V, P>> mFileChooser;

    /**
     * Returns the current layout resource id
     */
    @Override
    protected abstract int getLayoutResourceId(@Nullable Bundle bundle);

    /**
     * Here the {@link #findViewById(int)}
     */
    @Override
    protected abstract void initView();

    /**
     * Register event or unregister
     *
     * @param regEvent Current state
     */
    @Override
    protected abstract void regEvent(boolean regEvent);

    /**
     * Returns the current WebView instance
     *
     * @return {@link BaseWebView}
     */
    public abstract BaseWebView getWebView();

    /**
     * Perform page loading in {@link #initView()}
     */
    protected void loadUrl(WebParams params) {
        if (params != null) {
            loadUrl(params.path());
        }
    }

    /**
     * Perform load Web address in {@link #initView()}
     */
    protected void loadUrl(final String path) {
        post(new Runnable() {
            @Override
            public void run() {
                if (mWebView == null) {
                    MvpLog.e(TAG, "mWebView == null");
                    return;
                }
                mWebView.loadUrl(path);
            }
        });
    }

    protected void onHandleMessage(Message msg) {
    }

    protected BaseBridge<BaseWebActivity<V, P>> getBridge() {
        return null;
    }

    protected BaseWebViewClient<BaseWebActivity<V, P>> getWebViewClient() {
        return null;
    }

    protected BaseWebChromeClient<BaseWebActivity<V, P>> getWebChromeClient() {
        return null;
    }

    protected BaseFileChooser<BaseWebActivity<V, P>> getFileChooser() {
        return null;
    }

    protected final void post(Runnable task) {
        postDelayed(task, 0);
    }

    protected final void postDelayed(Runnable task, int delayMillis) {
        mHandler.postDelayed(task, delayMillis);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWebFrame();
    }

    /**
     * {@link BaseWebViewClient#shouldOverrideUrlLoading(WebView, WebResourceRequest)}
     */
    @Override
    public boolean shouldOverrideUrlLoading(String url) {
        MvpLog.e(TAG, "url: " + url);
        return false;
    }

    protected boolean webViewGoBack(boolean isFinish) {
        if (mWebView != null) {
            if (mWebView.webViewGoBack()) {
                return true;
            }
        }
        if (isFinish) {
            finish();
        }
        return false;
    }

    public final Handler getHandler() {
        return mHandler;
    }

    @Override
    public final boolean handleMessage(Message msg) {
        onHandleMessage(msg);
        if (mFileChooser != null) {
            mFileChooser.onHandleMessage(msg);
        }
        return false;
    }

    /**
     * Notify the WebView to reload
     */
    protected final void notifyWebViewReload() {
        if (mWebView != null) {
            mWebView.reload();
        }
    }

    /**
     * {@link BaseWebChromeClient#openFileChooser}
     */
    public final BaseFileChooser<BaseWebActivity<V, P>> createOrGetFileChooser() {
        if (mFileChooser == null) {
            mFileChooser = createFileChooser();
        }
        return mFileChooser;
    }

    @Override
    public final boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webViewGoBack(false)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    public void finish() {
        if (mWebView != null) {
            mWebView.loadUrl(SchemeConsts.BLANK);
        }
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        Log.e(TAG, "name: " + name);
        if (TextUtils.equals("", name)) {
            return mWebView = (BaseWebView) super.onCreateView(name, context, attrs);
        }
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.recycle();
        }
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mFileChooser != null) {
            mFileChooser.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initWebFrame() {
        final BaseWebView webView = getWebView();
        if (webView == null) {
            throw new NullPointerException("getWebView() == null");
        }
        initAndCreateBridge(webView);
    }

    private void initAndCreateBridge(BaseWebView webView) {
        SettingInit.initSetting(webView);
        createWebBridge(webView);
        createWebViewClient(webView);
        createWebChromeClient(webView);
        this.mWebView = webView;
    }

    private BaseFileChooser<BaseWebActivity<V, P>> createFileChooser() {
        final BaseFileChooser<BaseWebActivity<V, P>> fileChooser = Internal.createIfFileChooser(getFileChooser());
        fileChooser.attachTarget(this);
        return fileChooser;
    }

    private void createWebViewClient(WebView view) {
        final BaseWebViewClient<BaseWebActivity<V, P>> webViewClient = Internal.createIfWebviewClient(getWebViewClient());
        webViewClient.attachTarget(this);
        view.setWebViewClient(webViewClient);
    }

    private void createWebChromeClient(WebView view) {
        final BaseWebChromeClient<BaseWebActivity<V, P>> chromeClient = Internal.createIfWebChromeClick(getWebChromeClient());
        chromeClient.attachTarget(this);
        view.setWebChromeClient(chromeClient);
    }

    @SuppressLint("AddJavascriptInterface")
    private void createWebBridge(WebView view) {
        final BaseBridge<BaseWebActivity<V, P>> bridge = getBridge();
        if (bridge != null) {
            bridge.bindTarget(this, view);
            // 适用于 API Level 17及以后，之前有安全问题
            // todo 暂未做修复
//            view.addJavascriptInterface(bridge, bridge.bridgeName());
        }
    }
}
