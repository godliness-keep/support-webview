package com.longrise.android.web.internal.bridge;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;

import com.longrise.android.jssdk.core.bridge.BaseBridge;
import com.longrise.android.web.internal.IBridgeAgent;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020/9/2.
 *
 * @author godliness
 * WebView Bridge 桥梁
 */
public abstract class BaseWebBridge<T extends IBridgeAgent<T>> extends BaseBridge<T> {

    private Handler mHandler;
    private WeakReference<WebView> mView;

    @Override
    protected void onDestroy() {
    }

    public boolean onHandleMessage(Message msg) {
        return false;
    }

    protected final Handler getHandler() {
        return mHandler;
    }

    protected final void post(Runnable task) {
        post(task, 0);
    }

    protected final void post(Runnable task, int delayMillis) {
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
        super.bindTarget(target);
        // 适用于 API Level 17及以后，之前有安全问题，暂未做修复
        view.addJavascriptInterface(this, bridgeName());
        this.mHandler = target.getHandler();
        this.mView = new WeakReference<>(view);
    }
}
