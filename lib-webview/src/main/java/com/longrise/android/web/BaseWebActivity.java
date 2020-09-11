package com.longrise.android.web;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebView;

import com.longrise.android.web.internal.BaseWebView;
import com.longrise.android.web.internal.Internal;
import com.longrise.android.web.internal.SchemeConsts;
import com.longrise.android.web.internal.SettingInit;
import com.longrise.android.web.internal.bridge.BaseDownloader;
import com.longrise.android.web.internal.bridge.BaseFileChooser;
import com.longrise.android.web.internal.bridge.BaseWebBridge;
import com.longrise.android.web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.web.internal.bridge.BaseWebViewClient;
import com.longrise.android.web.internal.webcallback.WebLoadListener;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public abstract class BaseWebActivity<T extends BaseWebActivity<T>> extends AppCompatActivity implements
        WebLoadListener, Handler.Callback {

    private static final String TAG = "BaseWebActivity";

    private final Handler mHandler = new Handler(this);
    private BaseWebView mWebView;
    private BaseFileChooser<T> mFileChooser;

    /**
     * Returns the current layout resource id
     *
     * @param state {@link android.app.Activity#onCreate(Bundle)}
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
     * 需要在 {@link #setContentView(int)} 之前的逻辑
     */
    protected void beforeSetContentView() {

    }

    /**
     * 扩展 Bridge {@link BaseWebBridge}
     */
    protected BaseWebBridge<T> getBridge() {
        return null;
    }

    /**
     * 扩展 WebViewClient {@link BaseWebViewClient}
     */
    protected BaseWebViewClient<T> getWebViewClient() {
        return null;
    }

    /**
     * 扩展 WebChromeClient {@link BaseWebChromeClient}
     */
    protected BaseWebChromeClient<T> getWebChromeClient() {
        return null;
    }

    /**
     * 扩展 FileChooser {@link BaseFileChooser}
     */
    protected BaseFileChooser<T> getFileChooser() {
        return null;
    }

    /**
     * 扩展 WebView 下载 {@link WebView#setDownloadListener(DownloadListener)}
     */
    protected BaseDownloader<T> getDownloadHelper() {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webViewCanGoBack()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
    public final BaseFileChooser<T> createOrGetFileChooser() {
        if (mFileChooser == null) {
            mFileChooser = createFileChooser();
        }
        return mFileChooser;
    }

    @Override
    public final boolean handleMessage(Message msg) {
        if (isFinishing()) {
            return true;
        }
        if (onHandleMessage(msg)) {
            return true;
        }
        if (onClientHandleMessage(msg)) {
            return true;
        }
        return onChooserHandleMessage(msg);
    }

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beforeSetContentView();
        setContentView(getLayoutResourceId(savedInstanceState));
    }

    @Override
    public final void onContentChanged() {
        super.onContentChanged();
        initView();
        createWebFrame();
        regEvent(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
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
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.recycle();
            mWebView = null;
        }
        regEvent(false);
        mFileChooser = null;
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
        final BaseWebView webView = getWebView();
        if (webView == null) {
            throw new NullPointerException("getWebView() == null");
        }
        webView.registerCallback(this);
        initAndCreateBridge(webView);
    }

    private void initAndCreateBridge(BaseWebView webView) {
        SettingInit.initSetting(webView);
        createWebBridge(webView);
        createWebViewClient(webView);
        createWebChromeClient(webView);
        createDownloadHelper(webView);
        this.mWebView = webView;
    }

    private BaseFileChooser<T> createFileChooser() {
        final BaseFileChooser<T> fileChooser = Internal.createIfFileChooser(getFileChooser());
        fileChooser.attachTarget(this);
        return fileChooser;
    }

    private void createWebViewClient(WebView view) {
        final BaseWebViewClient<T> webViewClient = Internal.createIfWebViewClient(getWebViewClient());
        webViewClient.attachTarget(this, view);
    }

    private void createWebChromeClient(WebView view) {
        final BaseWebChromeClient<T> chromeClient = Internal.createIfWebChromeClick(getWebChromeClient());
        chromeClient.attachTarget(this, view);
    }

    private void createWebBridge(WebView view) {
        final BaseWebBridge<T> bridge = Internal.createIfBridge(getBridge());
        bridge.bindTarget(this, view);
    }

    private void createDownloadHelper(WebView view) {
        final BaseDownloader<T> downloader = getDownloadHelper();
        if (downloader != null) {
            downloader.attachTarget(this, view);
        }
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
