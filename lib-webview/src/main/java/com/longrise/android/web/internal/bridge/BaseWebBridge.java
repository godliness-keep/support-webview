package com.longrise.android.web.internal.bridge;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.longrise.android.jssdk.wx.bridge.BaseApiBridge;
import com.longrise.android.web.internal.IBridgeAgent;
import com.longrise.android.web.internal.IBridgeListener;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020/9/2.
 *
 * @author godliness
 * WebView Bridge 桥梁
 */
public abstract class BaseWebBridge<T extends IBridgeAgent<T>> extends BaseApiBridge<T> {

    private Handler mHandler;
    private WeakReference<WebView> mView;

    private IBridgeListener mClientBridge;

    @Override
    protected void onDestroy() {
    }

    public boolean onHandleMessage(Message msg) {
        return false;
    }

    protected final Handler getHandler() {
        return mHandler;
    }

    @Override
    protected final void post(Runnable task) {
        postDelayed(task, 0);
    }

    @Override
    protected final void postDelayed(Runnable task, int delayMillis) {
        if (!isFinished()) {
            mHandler.postDelayed(task, delayMillis);
        }
    }

    @Override
    protected final WebView getWebView() {
        return mView.get();
    }

    @SuppressLint("AddJavascriptInterface")
    public final void attachTarget(T target, WebView view) {
        super.attachTarget(target);
        // 适用于 API Level 17及以后，之前有安全问题，暂未做修复
        view.addJavascriptInterface(this, bridgeName());
        this.mHandler = target.getHandler();
        this.mView = new WeakReference<>(view);
    }

    @JavascriptInterface
    public final void onPageLoaded() {
        // 用于辅助完成页面加载成功的管理
        if (mClientBridge != null) {
            mClientBridge.onPageLoaded(mView.get());
        }
    }

    public final void invokeClientBridge(IBridgeListener agent) {
        this.mClientBridge = agent;
    }
}
