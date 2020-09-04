package com.longrise.android.web.internal.bridge;

import android.os.Handler;
import android.os.Message;

import com.longrise.android.jssdk_x5.core.bridge.BaseBridge;
import com.longrise.android.x5web.BaseWebActivity;
import com.tencent.smtt.sdk.WebView;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020/9/2.
 *
 * @author godliness
 */
public abstract class BaseWebBridge<T extends BaseWebActivity<T>> extends BaseBridge<T> {

    private Handler mHandler;
    private WeakReference<WebView> mView;

    @SuppressWarnings("unchecked")
    public final void bindTarget(BaseWebActivity<T> target, WebView view) {
        super.bindTarget((T) target);
        this.mHandler = target.getHandler();
        this.mView = new WeakReference<>(view);
    }

    /**
     * 对应{@link T#onDestroy()}
     */
    @Override
    protected abstract void onDestroy();

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
}
