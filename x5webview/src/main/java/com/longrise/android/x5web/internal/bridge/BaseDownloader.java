package com.longrise.android.x5web.internal.bridge;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import com.longrise.android.x5web.internal.IBridgeAgent;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebView;

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

    /**
     * Notify the host application that a file should be downloaded
     *
     * @param url                The full url to the content that should be downloaded
     * @param userAgent          the user agent to be used for the download.
     * @param contentDisposition Content-disposition http header, if
     *                           present.
     * @param mimeType           The mimetype of the content reported by the server
     * @param contentLength      The file size reported by the server
     */
    @Override
    public abstract void onDownloadStart(String url, String userAgent,
                                         String contentDisposition, String mimeType, long contentLength);

    /**
     * 对应{@link Activity#onDestroy()}
     */
    public abstract void onDestroy();

    /**
     * 获取当前 Activity {@link T}
     */
    @Nullable
    protected final T getTarget() {
        return mTarget.get();
    }

    /**
     * 当前 Activity 是否已经 finish {@link T#isFinishing()}
     */
    protected final boolean isFinished() {
        final T target = getTarget();
        return target == null || target.isFinishing();
    }

    /**
     * 获取 Handler 实例
     */
    protected final Handler getHandler() {
        return mHandler;
    }

    /**
     * 利用 Handler 发送任务
     */
    protected final void post(Runnable action) {
        postDelayed(action, 0);
    }

    /**
     * 利用 Handler 发送 Delay 任务
     */
    protected final void postDelayed(Runnable action, int delay) {
        if (!isFinished()) {
            mHandler.postDelayed(action, delay);
        }
    }

    /**
     * 拦截由 Handler 发送的消息
     *
     * @return true 表示拦截
     */
    public boolean onHandleMessage(Message msg) {
        return false;
    }

    public final void attachTarget(T target, WebView view) {
        view.setDownloadListener(this);
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
    }
}
