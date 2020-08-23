package com.longrise.android.web.internal.bridge;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
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

import com.longrise.android.mvp.utils.MvpLog;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.internal.Internal;
import com.longrise.android.web.internal.SchemeConsts;
import com.longrise.android.web.internal.webcallback.WebCallback;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class BaseWebViewClient<T extends BaseWebActivity> extends WebViewClient {

    private static final String TAG = "BaseWebViewClient";

    private Handler mHandler;
    private WeakReference<T> mTarget;
    private WeakReference<WebCallback.WebViewClientListener> mClientCallback;

    private boolean mBlockImageLoad;
    private boolean mLoadedError;
    private boolean mFirstFinished = true;

    public final void attachTarget(T target) {
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
        addWebViewClientListener(target);
    }

    @Nullable
    protected final T getTarget(){
        return mTarget.get();
    }

    protected final boolean isFinished() {
        return Internal.activityIsFinished(getTarget());
    }

    protected final void post(Runnable action) {
        postDelayed(action, 0);
    }

    protected final void postDelayed(Runnable action, int delay) {
        if (!isFinished()) {
            mHandler.postDelayed(action, delay);
        }
    }

    protected final WebCallback.WebViewClientListener getCallback() {
        return mClientCallback != null ? mClientCallback.get() : null;
    }

    /**
     * 1、第一次加载Url，该方法并不会被回调
     * 2、只有页面中超链接才会回调该方法
     * 3、window.location.href 不会调用该方法
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (isFinished()) {
            return true;
        }
        if (!redirectOverrideUrlLoading(view, url)) {
            return super.shouldOverrideUrlLoading(view, url);
        }
        //拦截
        return true;
    }

    /**
     * 5.0之后执行该方法
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (isFinished()) {
            return;
        }

        if (mLoadedError) {
            notifyWebViewLoadedState();
        }

        MvpLog.e(TAG, "onPageStarted");
//        blockImageLoad(view);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (isFinished()) {
            return;
        }

        notifyWebViewLoadedState();

        if (mFirstFinished) {
            clearForwardHistory(view);
            mFirstFinished = false;
        }

//        toImageLoad(view);
    }

    /**
     * File not found, no network connection, server not found for the main resource
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (interceptErrorState(view, failingUrl, errorCode)) {
            return;
        }
        this.mLoadedError = true;
        MvpLog.e(TAG, "onReceivedError : " + errorCode);
    }

    /**
     * File not found, no network connection, server not found for the main resource
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        final String description = (String) error.getDescription();
        final int errorCode = error.getErrorCode();
        MvpLog.e(TAG, "onReceivedError: " + " errorCode: " + errorCode + " description: " + description);
        final String failingUrl = request.getUrl().toString();
        if (interceptErrorState(view, failingUrl, errorCode)) {
            return;
        }

        this.mLoadedError = true;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

    /**
     * Api Level 23 Capture http error
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        //任何HTTP请求产生的错误都会回调这个方法，包括主页面的html文档请求，iframe、图片等资源请求。
        //在这个回调中，由于混杂了很多请求，不适合用来展示加载错误的页面，而适合做监控报警。
        //当某个URL，或者某个资源收到大量报警时，说明页面或资源可能存在问题，这时候可以让相关运营及时响应修改。
        final int errorCode = errorResponse.getStatusCode();
        MvpLog.e(TAG, "onReceivedHttpError: " + errorCode + " errored url: " + request.getUrl());
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
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

    private void addWebViewClientListener(WebCallback.WebViewClientListener clientListener) {
        this.mClientCallback = new WeakReference<>(clientListener);
    }

    private boolean interceptErrorState(WebView view, String failingUrl, int errorCode) {
        if (errorCode != -8 && errorCode != -2) {
            final boolean isErrorUrl = !TextUtils.equals(failingUrl, view.getUrl()) && !TextUtils.equals(failingUrl, view.getOriginalUrl());
            final boolean error = (failingUrl == null && errorCode != -12) || (errorCode == -1);
            return isErrorUrl || error;
        }
        return false;
    }

    private boolean beforeUrlLoading(String url) {
        final WebCallback.WebViewClientListener callback = getCallback();
        if (callback != null) {
            //Give the outside world a chance to intercept
            return callback.shouldOverrideUrlLoading(url);
        }
        return false;
    }

    private boolean redirectOverrideUrlLoading(WebView view, String url) {
        final boolean intercept = beforeUrlLoading(url);
        MvpLog.e(TAG, "intercept: " + intercept);
        if (intercept || TextUtils.isEmpty(url) || !isKnowScheme(url)) {
            //Unable to process Url address
            return true;
        }
        final WebView.HitTestResult hitTestResult = view.getHitTestResult();
        if (hitTestResult == null) {
            view.loadUrl(url);
            return true;
        }
        return false;
    }

    /**
     * 应该配置在xml
     */
    private boolean isKnowScheme(String url) {
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
                MvpLog.e(TAG, "clearForwardHistory");
            }
        }
    }

    private void notifyWebViewLoadedState() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final WebCallback.WebViewClientListener callback = getCallback();
                if (callback != null) {
                    if (mLoadedError) {
                        callback.loadFailed();
                        mLoadedError = false;
                    } else {
                        callback.loadSucceed();
                    }
                }
            }
        });
    }
}
