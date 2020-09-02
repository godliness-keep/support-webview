package com.longrise.android.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.webkit.WebView;

import com.longrise.android.jssdk.core.bridge.BaseBridge;
import com.longrise.android.mvp.internal.BaseMvpActivity;
import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
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
    private BaseWebView<V, P> mWebView;
    private BaseFileChooser<V, P, BaseWebActivity<V, P>> mFileChooser;

    /**
     * Returns the current layout resource id
     *
     * @param state {@link android.app.Activity#onCreate(Bundle)}
     * @return The current layout id
     */
    @Override
    protected abstract int getLayoutResourceId(@Nullable Bundle state);

    /**
     * Here the {@link #findViewById(int)}
     * 如果需要对 Window 进行操作 {@link #beforeSetContentView()}
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
    public abstract BaseWebView<V, P> getWebView();

    /**
     * Perform load Web address in {@link #initView()}
     */
    protected void loadUrl(String url) {
        post(loadTask(url));
    }

    /**
     * 获取 Handler 实例
     */
    public final Handler getHandler() {
        return mHandler;
    }

    /**
     * 拦截通过 Handler 发送的任务
     */
    protected boolean onHandleMessage(Message msg) {
        return false;
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

    /**
     * 扩展 Bridge {@link BaseBridge}
     */
    protected BaseBridge<BaseWebActivity<V, P>> getBridge() {
        return null;
    }

    /**
     * 扩展 WebViewClient {@link BaseWebViewClient}
     */
    protected BaseWebViewClient<V, P, BaseWebActivity<V, P>> getWebViewClient() {
        return null;
    }

    /**
     * 扩展 WebChromeClient {@link BaseWebChromeClient}
     */
    protected BaseWebChromeClient<V, P, BaseWebActivity<V, P>> getWebChromeClient() {
        return null;
    }

    /**
     * 扩展 FileChooser {@link BaseFileChooser}
     */
    protected BaseFileChooser<V, P, BaseWebActivity<V, P>> getFileChooser() {
        return null;
    }

    /**
     * 用于拦截 URL 加载 {@link BaseWebViewClient#shouldOverrideUrlLoading(WebView, String)}
     *
     * @return true 表示拦截本次加载，由开发人员实现
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
    public final BaseFileChooser<V, P, BaseWebActivity<V, P>> createOrGetFileChooser() {
        if (mFileChooser == null) {
            mFileChooser = createFileChooser();
        }
        return mFileChooser;
    }

    @Override
    public final boolean handleMessage(Message msg) {
        if (onHandleMessage(msg)) {
            return true;
        }
        if (onClientHandleMessage(msg)) {
            return true;
        }
        return onChooserHandleMessage(msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webViewCanGoBack()) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createWebFrame();
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

    private void createWebFrame() {
        final BaseWebView<V, P> webView = getWebView();
        if (webView == null) {
            throw new NullPointerException("getWebView() == null");
        }
        initAndCreateBridge(webView);
    }

    private void initAndCreateBridge(BaseWebView<V, P> webView) {
        SettingInit.initSetting(webView);
        createWebBridge(webView);
        createWebViewClient(webView);
        createWebChromeClient(webView);
        this.mWebView = webView;
    }

    private BaseFileChooser<V, P, BaseWebActivity<V, P>> createFileChooser() {
        final BaseFileChooser<V, P, BaseWebActivity<V, P>> fileChooser = Internal.createIfFileChooser(getFileChooser());
        fileChooser.attachTarget(this);
        return fileChooser;
    }

    private void createWebViewClient(WebView view) {
        final BaseWebViewClient<V, P, BaseWebActivity<V, P>> webViewClient = Internal.createIfWebviewClient(getWebViewClient());
        webViewClient.attachTarget(this);
        view.setWebViewClient(webViewClient);
    }

    private void createWebChromeClient(WebView view) {
        final BaseWebChromeClient<V, P, BaseWebActivity<V, P>> chromeClient = Internal.createIfWebChromeClick(getWebChromeClient());
        chromeClient.attachTarget(this);
        view.setWebChromeClient(chromeClient);
    }

    @SuppressLint("AddJavascriptInterface")
    private void createWebBridge(WebView view) {
        final BaseBridge<BaseWebActivity<V, P>> bridge = Internal.createIfBridge(getBridge());
        bridge.bindTarget(this, view);
        // 适用于 API Level 17及以后，之前有安全问题，暂未做修复
        view.addJavascriptInterface(bridge, bridge.bridgeName());
    }

    private boolean onClientHandleMessage(Message msg) {
        return mWebView != null && mWebView.onHandleMessage(msg);
    }

    private boolean onChooserHandleMessage(Message msg) {
        return mFileChooser != null && mFileChooser.onHandleMessage(msg);
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
