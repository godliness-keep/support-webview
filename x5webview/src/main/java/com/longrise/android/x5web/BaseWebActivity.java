package com.longrise.android.x5web;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.longrise.android.jssdk_x5.core.bridge.BaseBridge;
import com.longrise.android.x5web.internal.IBridgeAgent;
import com.longrise.android.x5web.internal.Internal;
import com.longrise.android.x5web.internal.SchemeConsts;
import com.longrise.android.x5web.internal.X5WebView;
import com.longrise.android.x5web.internal.bridge.BaseDownloader;
import com.longrise.android.x5web.internal.bridge.BaseWebBridge;
import com.longrise.android.x5web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.x5web.internal.bridge.BaseWebViewClient;
import com.longrise.android.x5web.internal.webcallback.IWebLoadListener;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebView;


/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public abstract class BaseWebActivity<T extends BaseWebActivity<T>> extends AppCompatActivity implements
        IWebLoadListener, Handler.Callback, IBridgeAgent<T> {

    private static final String TAG = "BaseWebActivity";

    private final Handler mHandler = new Handler(this);
    private X5WebView mWebView;

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
     * @return {@link X5WebView}
     */
    public abstract X5WebView getWebView();

    /**
     * Perform load Web address in {@link #initView()}
     */
    protected void loadUrl(String url) {
        post(loadTask(url));
    }

    /**
     * ????????? {@link #setContentView(int)} ???????????????
     */
    protected void beforeSetContentView() {

    }

    /**
     * ?????? Bridge {@link BaseBridge}
     */
    @Override
    public BaseWebBridge<T> getBridge() {
        return null;
    }

    /**
     * ?????? WebViewClient {@link BaseWebViewClient}
     */
    @Override
    public BaseWebViewClient<T> getWebViewClient() {
        return null;
    }

    /**
     * ?????? WebChromeClient {@link BaseWebChromeClient}
     */
    @Override
    public BaseWebChromeClient<T> getWebChromeClient() {
        return null;
    }

    /**
     * ?????? WebView ?????? {@link WebView#setDownloadListener(DownloadListener)}
     */
    @Override
    public BaseDownloader<T> getDownloadHelper() {
        return null;
    }

    /**
     * ???????????? URL ?????? {@link BaseWebViewClient#shouldOverrideUrlLoading(WebView, String)}
     *
     * @return true ????????????????????????
     */
    @Override
    public boolean shouldOverrideUrlLoading(String url) {
        return false;
    }

    /**
     * ?????? WebView ??????
     *
     * @return true ????????? WebView ?????????????????????????????????
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
     * ?????? Handler ??????
     */
    public final Handler getHandler() {
        return mHandler;
    }

    /**
     * ???????????? Handler ???????????????
     */
    protected boolean onHandleMessage(Message msg) {
        return false;
    }

    /**
     * ?????? Handler ????????????
     */
    protected final void post(Runnable task) {
        postDelayed(task, 0);
    }

    /**
     * ?????? Handler ?????? Delay ??????
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
            mWebView = null;
        }
        regEvent(false);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    private void createWebFrame() {
        final X5WebView webView = getWebView();
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
