package com.longrise.android.web.internal;

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
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
import com.longrise.android.mvp.utils.MvpLog;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.BuildConfig;
import com.longrise.android.web.helper.WebViewDownloadHelper;
import com.longrise.android.web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.web.internal.bridge.BaseWebViewClient;

import java.lang.reflect.Method;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
@SuppressWarnings("unused")
public class BaseWebView<V extends BaseView, P extends BasePresenter<V>> extends WebView implements DownloadListener {

    public static final String NAME = BaseWebView.class.getName();

    private static final int SCROLL_TOP = 0;
    private static final int SCROLL_END = 1;

    private static final String[] WEB_VIEW_EXCEPTION = {
            "android.content.pm.PackageManager$NameNotFoundException",
            "java.lang.RuntimeException: Cannot load WebView",
            "android.webkit.WebViewFactory$MissingWebViewPackageException: Failed to load WebView provider: No WebView installed"};

    private IScrollChangeListener mScrollListener;
    private WebViewDownloadHelper mDownloadHelper;

    private BaseWebChromeClient<V, P, BaseWebActivity<V, P>> mWebChromeClient;
    private BaseWebViewClient<V, P, BaseWebActivity<V, P>> mWebViewClient;

    public BaseWebView(Context context) {
        this(context, null);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDownloadListener(this);
        removeJavascriptInterfaces();
        disableAccessibility(context);
    }

    @Nullable
    public static <V extends BaseView, P extends BasePresenter<V>> BaseWebView<V, P> createOrGetWebView(Context context) {
        return WebViewFactory.findWebView(context);
    }

    @Nullable
    public static <V extends BaseView, P extends BasePresenter<V>> BaseWebView<V, P> createOrGetWebViewAndInitSetting(Context context) {
        final BaseWebView<V, P> webView = WebViewFactory.findWebView(context);
        if (webView != null) {
            SettingInit.initSetting(webView);
        }
        return webView;
    }

    /**
     * 设置监听 WebView 滑动
     */
    public void setScrollChangedListener(IScrollChangeListener changedListener) {
        this.mScrollListener = changedListener;
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @Override
    public void addJavascriptInterface(Object object, String name) {
        super.addJavascriptInterface(object, name);
    }

    @Override
    protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
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
    public void loadUrl(String url) {
        try {
            super.loadUrl(url);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public final boolean onHandleMessage(Message msg) {
        if (mWebViewClient != null && mWebViewClient.onHandleMessage(msg)) {
            return true;
        }
        return mWebChromeClient != null && mWebChromeClient.onHandleMessage(msg);
    }

    public final boolean webViewGoBack() {
        if (canGoBack() && canGoBackOrForward(-1)) {
            goBack();
            //isRedirect(getUrl()) ||
            if (isBlankUrl(getOriginalUrl())) {
                return webViewGoBack();
            }
            return true;
        }
        return false;
    }

    @Override
    public void setOverScrollMode(int mode) {
        try {
            super.setOverScrollMode(mode);
        } catch (Throwable throwable) {
            final String messageCause = throwable.getCause() == null ? throwable.toString() : throwable.getCause().toString();
            final String trace = Log.getStackTraceString(throwable);
            for (String exception : WEB_VIEW_EXCEPTION) {
                if (trace.contains(exception)) {
                    throwable.printStackTrace();
                    //此时 WebView 处于不可用状态
                    return;
                }
            }
            if (throwable instanceof NoClassDefFoundError) {
                // java.lang.NoClassDefFoundError android/webkit/JniUtil
                throwable.printStackTrace();
                // 此时 WebView 处于不可用状态
                return;
            }
            throw throwable;
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public boolean isPrivateBrowsingEnabled() {
        return !(Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && getSettings() == null) && super.isPrivateBrowsingEnabled();
    }

    public final void clearMemoryCache() {
        clearCache(false);
    }

    public final void clearAllCache() {
        clearCache(true);
    }

    @Override
    public void destroy() {
        release();
        super.destroy();
        final Drawable drawable = getBackground();
        if (drawable != null) {
            setBackground(null);
            drawable.setCallback(null);
        }
    }

    public final void recycle() {
        final boolean recycled = WebViewFactory.recycle(this);
        if (recycled) {
            release();
        } else {
            destroy();
        }

        if (mDownloadHelper != null) {
            mDownloadHelper.uninstallDownloadHelper();
        }
        mDownloadHelper = null;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if (mDownloadHelper == null) {
            mDownloadHelper = WebViewDownloadHelper.installDownloadHelper(getContext());
        }
        mDownloadHelper.addDownloadTask(url, contentDisposition);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
        if (client instanceof BaseWebChromeClient) {
            this.mWebChromeClient = (BaseWebChromeClient<V, P, BaseWebActivity<V, P>>) client;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
        if (client instanceof BaseWebViewClient) {
            this.mWebViewClient = (BaseWebViewClient<V, P, BaseWebActivity<V, P>>) client;
        }
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
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
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
                    MvpLog.print(e);
                }
            }
        }
    }

//    private boolean isRedirect(String current) {
//        if (mWebChromeClient != null) {
//            return mWebChromeClient.isRedirect(current);
//        }
//        return false;
//    }

    private boolean isBlankUrl(String current) {
        return TextUtils.equals(current, SchemeConsts.BLANK);
    }

    private void release() {
        final ViewParent viewParent = getParent();
        if (viewParent instanceof ViewGroup) {
            ((ViewGroup) viewParent).removeView(this);
        }
        stopLoading();
        clearMatches();
        getSettings().setJavaScriptEnabled(false);
        clearView();
        removeAllViews();
        setWebChromeClient(null);
        setWebViewClient(null);
        clearHistory();
    }
}
