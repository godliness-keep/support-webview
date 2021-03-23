package com.longrise.android.web.internal.bridge;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.webkit.DownloadListener;
import android.webkit.WebView;

import com.longrise.android.web.internal.IBridgeAgent;

import java.lang.ref.WeakReference;


/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 * 负责触发需要下载的相关
 */
public abstract class BaseDownloader<T extends IBridgeAgent<?>> implements DownloadListener {

    private Handler mHandler;
    private WeakReference<T> mTarget;

    @Override
    public abstract void onDownloadStart(String url, String userAgent,
                                         String contentDisposition, String mimeType, long contentLength);

    public abstract void onDestroy();

    public boolean onHandleMessage(Message msg) {
        return false;
    }

    @Nullable
    protected final T getTarget() {
        return mTarget.get();
    }

    protected final boolean isFinished() {
        final T target = getTarget();
        return target == null || target.isFinishing();
    }

    protected final Handler getHandler() {
        return mHandler;
    }

    protected final void post(Runnable action) {
        postDelayed(action, 0);
    }

    protected final void postDelayed(Runnable action, int delay) {
        if (!isFinished()) {
            mHandler.postDelayed(action, delay);
        }
    }

    public final void attachTarget(T target, WebView view) {
        view.setDownloadListener(this);
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
    }
}
