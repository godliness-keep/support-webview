package com.longrise.android.x5web.internal.bridge;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.longrise.android.jsapi_x5.wx.bridge.BaseApiBridge;
import com.longrise.android.x5web.internal.IBridgeAgent;
import com.tencent.smtt.sdk.WebView;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020/9/2.
 *
 * @author godliness
 */
public abstract class BaseWebBridge<T extends IBridgeAgent<T>> extends BaseApiBridge<T> {

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
        postDelayed(task, 0);
    }

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
        super.attachTarget((T) target);
        // 适用于 API Level 17及以后，之前有安全问题，暂未做修复
        view.addJavascriptInterface(this, bridgeName());
        this.mHandler = target.getHandler();
        this.mView = new WeakReference<>(view);
    }
}
