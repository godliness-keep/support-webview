package com.longrise.android.x5web.internal;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;

import com.longrise.android.x5web.X5;
import com.longrise.android.x5web.internal.bridge.BaseDownloader;
import com.longrise.android.x5web.internal.bridge.BaseWebBridge;
import com.longrise.android.x5web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.x5web.internal.bridge.BaseWebViewClient;
import com.longrise.android.x5web.internal.webcallback.WebLoadListener;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.reflect.Method;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public class X5WebView extends WebView {

    private static final int SCROLL_TOP = 0;
    private static final int SCROLL_END = 1;

    private static final String[] WEB_VIEW_EXCEPTION = {
            "android.content.pm.PackageManager$NameNotFoundException",
            "java.lang.RuntimeException: Cannot load WebView",
            "android.webkit.WebViewFactory$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed"};

    private IScrollChangeListener mScrollListener;

    private BaseWebChromeClient<?> mWebChromeClient;
    private BaseWebViewClient<?> mWebViewClient;
    private BaseDownloader<?> mDownloader;
    private BaseWebBridge<?> mBridge;

    private OnBridgeListener mClientBridge;

    public X5WebView(Context context) {
        this(context, null);
    }

    public X5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        removeJavascriptInterfaces();
        disableAccessibility(context);

        this.mClientBridge = ClientBridgeAgent.getInstance();
    }

    /**
     * 快速获取一个 X5WebView
     */
    @Nullable
    public static X5WebView createOrGetWebView(Context context) {
        return WebViewFactory.findWebView(context);
    }

    /**
     * 快速获取一个 X5WebView，并设置 WebSetting
     */
    @Nullable
    public static X5WebView createOrGetWebViewAndInitSetting(Context context) {
        final X5WebView webView = WebViewFactory.findWebView(context);
        if (webView != null) {
            SettingInit.initSetting(webView);
        }
        return webView;
    }

    /**
     * 设置监听 WebView 滑动
     */
    public final void setScrollChangedListener(IScrollChangeListener changedListener) {
        this.mScrollListener = changedListener;
    }

    /**
     * 清除内存缓存
     */
    public final void clearMemoryCache() {
        clearCache(false);
    }

    /**
     * 清除全部缓存，包括文件缓存
     */
    public final void clearAllCache() {
        clearCache(true);
    }

    /**
     * WebView 是否可以回退
     */
    public final boolean webViewGoBack() {
        if (canGoBack() && canGoBackOrForward(-1)) {
            goBack();
            if (isBlankUrl(getOriginalUrl())) {
                return webViewGoBack();
            }
            return true;
        }
        return false;
    }

    public void registerCallback(WebLoadListener webCallback) {
        mClientBridge.registerCallback(webCallback);
    }

    @Override
    public final void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
        if (client instanceof BaseWebChromeClient<?>) {
            this.mWebChromeClient = (BaseWebChromeClient<?>) client;
            this.mWebChromeClient.invokeClientBridge(mClientBridge);
        }
    }

    @Override
    public final void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
        if (client instanceof BaseWebViewClient) {
            this.mWebViewClient = (BaseWebViewClient<?>) client;
            this.mWebViewClient.invokeClientBridge(mClientBridge);
        }
    }

    @Override
    public void setDownloadListener(DownloadListener downloadListener) {
        super.setDownloadListener(downloadListener);
        if (downloadListener instanceof BaseDownloader) {
            this.mDownloader = (BaseDownloader<?>) downloadListener;
        }
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @Override
    public void addJavascriptInterface(Object object, String name) {
        super.addJavascriptInterface(object, name);
        if (object instanceof BaseWebBridge) {
            this.mBridge = (BaseWebBridge<?>) object;
        }
    }

    /**
     * 回收 WebView，区别与 destroy，被回收的 WebView 可以二次复用
     * 复用版的暂未完善
     */
    public final void recycle() {
//        final boolean recycled = WebViewFactory.recycle(this);
//        if (recycled) {
//            release();
//        } else {
        destroy();
//        }

        if (mDownloader != null) {
            mDownloader.onDestroy();
            mDownloader = null;
        }
    }

    /**
     * 销毁 WebView
     */
    @Override
    public void destroy() {
        release();
        super.destroy();
        final Drawable drawable = getBackground();
        if (drawable != null) {
            setBackground(null);
            drawable.setCallback(null);
        }
        if (mClientBridge != null) {
            mClientBridge.destroy();
            mClientBridge = null;
        }
    }

    @Override
    protected final void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        super.onScrollChanged(left, top, oldLeft, oldTop);
        if (mScrollListener != null) {
            float webContent = getContentHeight() * getScale();
            float webNow = getHeight() + getScrollY();
            if (Math.abs(webContent - webNow) < SCROLL_END) {
                mScrollListener.onScrollEnd(left, top, oldLeft, oldTop);
            } else if (getScrollY() == SCROLL_TOP) {
                mScrollListener.onScrollTop(left, top, oldLeft, oldTop);
            } else {
                mScrollListener.onScroll(left, top, oldLeft, oldTop);
            }
        }
    }

    @Override
    public final void loadUrl(String url) {
        try {
            super.loadUrl(url);
        } catch (Exception e) {
            X5.print(e);
        }
    }

    public final boolean onHandleMessage(Message msg) {
        if (mBridge != null && mBridge.onHandleMessage(msg)) {
            return true;
        }
        if (mWebViewClient != null && mWebViewClient.onHandleMessage(msg)) {
            return true;
        }
        if (mWebChromeClient != null && mWebChromeClient.onHandleMessage(msg)) {
            return true;
        }
        return mDownloader != null && mDownloader.onHandleMessage(msg);
    }

    @Override
    public final void setOverScrollMode(int mode) {
        try {
            super.setOverScrollMode(mode);
        } catch (Throwable throwable) {
//            final String messageCause = throwable.getCause() == null ? throwable.toString() : throwable.getCause().toString();
            final String trace = Log.getStackTraceString(throwable);
            for (String exception : WEB_VIEW_EXCEPTION) {
                if (trace.contains(exception)) {
                    X5.print(throwable);
                    //此时 WebView 处于不可用状态
                    return;
                }
            }
            if (throwable instanceof NoClassDefFoundError) {
                // java.lang.NoClassDefFoundError android/webkit/JniUtil
                X5.print(throwable);
                // 此时 WebView 处于不可用状态
                return;
            }
            throw throwable;
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public final boolean isPrivateBrowsingEnabled() {
        return !(Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && getSettings() == null) && super.isPrivateBrowsingEnabled();
    }

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(11)
    private void removeJavascriptInterfaces() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                removeJavascriptInterface("searchBoxJavaBridge_");
                removeJavascriptInterface("accessibility");
                removeJavascriptInterface("accessibilityTraversal");
            } catch (Exception e) {
                X5.print(e);
            }
        }
    }

    /**
     * 问题主要在 4.2.1 和 4.2.2 比较集中，关闭辅助功能
     * <p>
     * {@link AccessibilityManager STATE_FLAG_ACCESSIBILITY_ENABLED}
     * java.lang.NullPointerException
     * at android.webkit.AccessibilityInjector$TextToSpeechWrapper$1
     * .onInit(AccessibilityInjector.java:753)
     */
    private void disableAccessibility(Context context) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            /*4.2 (Build.VERSION_CODES.JELLY_BEAN_MR1)*/
            if (context != null) {
                final AccessibilityManager am = (AccessibilityManager) context.getApplicationContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
                if (am == null || !am.isEnabled()) {
                    //Not need to disable accessibility
                    return;
                }
                try {
                    final Method setState = am.getClass().getDeclaredMethod("setState", int.class);
                    setState.setAccessible(true);
                    setState.invoke(am, 0);
                } catch (Exception e) {
                    X5.print(e);
                }
            }
        }
    }


    private boolean isBlankUrl(String current) {
        return TextUtils.equals(current, SchemeConsts.BLANK);
    }

    private void release() {
        final ViewParent viewParent = getParent();
        if (viewParent instanceof ViewGroup) {
            try {
                ((ViewGroup) viewParent).removeView(this);
            } catch (Exception e) {
                // X5 这里居然有异常
                if (X5.isDebug()) {
                    throw e;
                }
            }
        }
        stopLoading();
        clearMatches();
        getSettings().setJavaScriptEnabled(false);
        clearView();
        removeAllViews();
        setWebChromeClient(null);
        setWebViewClient(null);
        setDownloadListener(null);
        clearHistory();
    }
}
