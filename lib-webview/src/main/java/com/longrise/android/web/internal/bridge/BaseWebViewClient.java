package com.longrise.android.web.internal.bridge;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.internal.ClientBridgeAgent;
import com.longrise.android.web.internal.Internal;
import com.longrise.android.web.internal.SchemeConsts;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 * 如果不设置 WebViewClient 则由系统（Activity Manager）处理该 URL
 * 通常是使用浏览器打开或弹出选择对话框
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseWebViewClient<T extends BaseWebActivity<T>> extends WebViewClient {

    private static final String TAG = "BaseWebViewClient";

    private Handler mHandler;
    private WeakReference<BaseWebActivity<T>> mTarget;
    private ClientBridgeAgent mClientBridge;

    private boolean mBlockImageLoad;
    private boolean mFirstFinished = true;

    /**
     * 是否优先加载 DOM，暂时阻塞图片加载
     */
    protected boolean isPriorityResourceLoad() {
        return false;
    }

    /**
     * 获取当前 Activity
     */
    @SuppressWarnings("unchecked")
    @Nullable
    protected final T getTarget() {
        return (T) mTarget.get();
    }

    /**
     * 判断当前所依附的 Activity 是否已经 Finished
     */
    protected final boolean isFinished() {
        return Internal.activityIsFinished(getTarget());
    }

    /**
     * 获取 Handler 实例
     * 注：每个 Web 页面实例都有且共用一个 Handler 实例
     * 即在 Activity、WebChromeClient、WebViewClient、FileChooser 之间
     * 发送的消息可以相互接收到
     */
    protected final Handler getHandler() {
        return mHandler;
    }

    /**
     * 拦截通过 Handler 发送的消息
     * 注：每个实例都有且共用同一个 Handler 实例
     */
    public boolean onHandleMessage(Message msg) {
        return false;
    }

    /**
     * 通过 Handler 发送任务
     */
    protected final void post(Runnable action) {
        postDelayed(action, 0);
    }

    /**
     * 通过 Handler 发送一个 Delay 任务
     */
    protected final void postDelayed(Runnable action, int delay) {
        if (!isFinished()) {
            mHandler.postDelayed(action, delay);
        }
    }

    /**
     * shouldOverrideUrlLoading 规则较为繁琐，简单整理如下
     * 1、通过 loadUrl 该方法不会被回调
     * 2、WebView 的前进、后退、刷新、以及 POST 请求都不会回调
     * 3、重定向不会回调（例如：window.location.href）
     * <p>
     * 4、只有页面中超链接才会回调该方法
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        // 关于返回值的作用正确解读应该是
        // 如果返回 true，表明由应用自行（开发者）处理（拦截），WebView 不处理
        // 如果返回 false，则说明由 WebView 处理该 URL，即使用 WebView loadUrl
        return !canOverrideUrlLoading(webView, url);
    }

    /**
     * 5.0 之后执行该方法
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        return super.shouldOverrideUrlLoading(webView, webResourceRequest);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (isFinished()) {
            return;
        }

        if (mClientBridge != null) {
            mClientBridge.onPageStarted();
        }

        if (isPriorityResourceLoad()) {
            blockImageLoad(view);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (isFinished()) {
            return;
        }

        if (mClientBridge != null) {
            mClientBridge.onPageFinished();
        }

        if (mFirstFinished) {
            clearForwardHistory(view);
            mFirstFinished = false;
        }

        if (isPriorityResourceLoad()) {
            toImageLoad(view);
        }
    }

    /**
     * File not found, no network connection, server not found for the main resource
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (interceptErrorState(view, failingUrl, errorCode)) {
            return;
        }
        if (mClientBridge != null) {
            mClientBridge.onReceivedError();
        }
    }

    /**
     * File not found, no network connection, server not found for the main resource
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        final int errorCode = error.getErrorCode();
        final String failingUrl = request.getUrl().toString();
        if (interceptErrorState(view, failingUrl, errorCode)) {
            return;
        }
        if (mClientBridge != null) {
            mClientBridge.onReceivedError();
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        // WebView 中所有的资源加载都会走这里
        super.onLoadResource(view, url);
    }

    /**
     * Api Level 23 Capture http error
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        // 任何 HTTP 请求产生的错误都会回调这个方法，包括主页面的html文档请求，iframe、图片等资源请求。
        // 在这个回调中，由于混杂了很多请求，不适合用来展示加载错误的页面，而适合做监控报警。
        // 当某个 URL，或者某个资源收到大量报警时，说明页面或资源可能存在问题，这时候可以让相关运营及时响应修改。
    }

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError sslError) {
        if (handler != null) {
            handler.proceed();
        }
//        if (UrlUtils.isHost(getUrl())) {
//            handler.proceed();
//        } else {
//            super.onReceivedSslError(view, handler, error);
//        }
    }

    /**
     * 阻塞图片加载
     */
    private void blockImageLoad(WebView view) {
        if (!mBlockImageLoad) {
            final WebSettings settings = view.getSettings();
            if (settings != null) {
                settings.setBlockNetworkImage(true);
            }
            mBlockImageLoad = true;
        }
    }

    /**
     * 开始图片加载
     */
    private void toImageLoad(WebView view) {
        if (mBlockImageLoad) {
            final WebSettings settings = view.getSettings();
            if (settings != null) {
                settings.setBlockNetworkImage(false);
            }
            mBlockImageLoad = false;
        }
    }

    private boolean interceptErrorState(WebView view, String failingUrl, int errorCode) {
        if (errorCode != -8 && errorCode != -2) {
            final boolean isErrorUrl = !TextUtils.equals(failingUrl, view.getUrl()) && !TextUtils.equals(failingUrl, view.getOriginalUrl());
            final boolean error = (failingUrl == null && errorCode != -12) || (errorCode == -1);
            return isErrorUrl || error;
        }
        return false;
    }

    private boolean canOverrideUrlLoading(WebView view, String url) {
        if (isFinished()) {
            return false;
        }
        return canRedirectOverrideUrlLoading(view, url);
    }

    private boolean beforeUrlLoading(String url) {
        if (mClientBridge != null) {
            // 给外部一次拦截的机会
            return mClientBridge.beforeUrlLoading(url);
        }
        return false;
    }

    private boolean canRedirectOverrideUrlLoading(WebView view, String url) {
        final boolean intercept = beforeUrlLoading(url);
        if (intercept || TextUtils.isEmpty(url) || !isEffectiveScheme(url)) {
            // Unable to process Url address
            return false;
        }

        // 重定向
        final WebView.HitTestResult result = view.getHitTestResult();
        return result == null || result.getExtra() != null;
    }

    /**
     * 是否是有效的 scheme，如果是自定义动作 {@link #beforeUrlLoading(String)}
     */
    private boolean isEffectiveScheme(String url) {
        return url.startsWith(SchemeConsts.HTTP)
                || url.startsWith(SchemeConsts.HTTPS)
                || url.startsWith(SchemeConsts.FILE);
    }

    private boolean interceptAddressType(String url) {
        return url.endsWith(".apk")
                || url.endsWith(".css")
                || url.endsWith(".png")
                || url.endsWith(".jpg")
                || url.endsWith(".gif")
                || url.endsWith(".js");
    }

    private void clearForwardHistory(WebView view) {
        final WebBackForwardList forwardList = view.copyBackForwardList();
        if (forwardList != null && forwardList.getSize() > 1) {
            final WebHistoryItem item = forwardList.getItemAtIndex(0);
            if (item != null && TextUtils.equals(item.getUrl(), SchemeConsts.BLANK)) {
                view.clearHistory();
            }
        }
    }

    public final void invokeClientBridge(ClientBridgeAgent agent) {
        this.mClientBridge = agent;
    }

    public final void attachTarget(BaseWebActivity<T> target, WebView view) {
        view.setWebViewClient(this);
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
    }
}
