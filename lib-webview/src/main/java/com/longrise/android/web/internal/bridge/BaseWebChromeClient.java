package com.longrise.android.web.internal.bridge;

import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.BuildConfig;
import com.longrise.android.web.internal.Internal;
import com.longrise.android.web.internal.SchemeConsts;
import com.longrise.android.web.internal.webcallback.WebCallback;

import java.lang.ref.WeakReference;


/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public abstract class BaseWebChromeClient<V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> extends WebChromeClient {

    private static final String TAG = "BaseWebChromeClient";

    private Handler mHandler;
    private WeakReference<T> mTarget;
    private WeakReference<WebCallback.WebChromeListener> mWebChromeCallback;

    private boolean mFirstLoad = true;
//    private final ArraySet<Integer> mUrlHash = new ArraySet<>(5);

    /**
     * 获取当前 Activity 实例
     */
    @Nullable
    protected final T getTarget() {
        return mTarget.get();
    }

//    /**
//     * 判断是否为重定向地址
//     */
//    public final boolean isRedirect(String url) {
//        if (url != null) {
//            return !mUrlHash.remove(url.hashCode());
//        }
//        return false;
//    }

    /**
     * 判断当前宿主 Activity 是否已经 Finished
     */
    protected final boolean isFinished() {
        final boolean isAlive = Internal.activityIsFinished(mTarget.get());
        if (isAlive) {
            mHandler.removeCallbacksAndMessages(null);
        }
        return isAlive;
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
    protected final void post(Runnable task) {
        postDelayed(task, 0);
    }

    /**
     * 通过 Handler 发送 Delay 任务
     */
    protected final void postDelayed(Runnable task, int delay) {
        if (!isFinished()) {
            mHandler.postDelayed(task, delay);
        }
    }

    /**
     * 获取对外通知的 Callback
     */
    @Nullable
    protected final WebCallback.WebChromeListener getCallback() {
        return mWebChromeCallback != null ? mWebChromeCallback.get() : null;
    }

    @Override
    public void onReceivedTitle(WebView view, final String title) {
        if (isFinished()) {
            return;
        }
        if (TextUtils.equals(title, SchemeConsts.BLANK)) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final WebCallback.WebChromeListener callback = getCallback();
                if (callback != null) {
                    callback.onReceivedTitle(title);
                }
            }
        });
    }

    @Override
    public void onProgressChanged(WebView view, final int newProgress) {
        Log.e(TAG, "url: " + view.getUrl());
        if (isFinished()) {
            return;
        }
        // 管理重定向问题
//        overrideHistoryStack(view);

        post(new Runnable() {
            @Override
            public void run() {
                final WebCallback.WebChromeListener callback = getCallback();
                if (callback != null) {
                    callback.onProgressChanged(newProgress);
                }
            }
        });

        if (mFirstLoad) {
            view.clearHistory();
            mFirstLoad = false;
        }
    }

    /**
     * For android version < 3.0
     */
    public final void openFileChooser(ValueCallback<Uri> uploadMsg) {
        this.openFileChooser(uploadMsg, "*/*");
    }

    /**
     * For android version >= 3.0
     */
    public final void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        this.openFileChooser(uploadMsg, acceptType, null);
    }

    /**
     * For android version >= 4.1
     */
    public final void openFileChooser(final ValueCallback<Uri> uploadMsg, final String acceptType, final String capture) {
        if (isFinished()) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                final T target = getTarget();
                if (target != null) {
                    final BaseFileChooser<V, P, BaseWebActivity<V, P>> fileChooser = target.createOrGetFileChooser();
                    if (fileChooser != null) {
                        fileChooser.openFileChooser(uploadMsg, acceptType, capture);
                    }
                }
            }
        });
    }

    /**
     * For android version >= 5.0  todo 4.4版本是个 Bug 无回调，需要另行约定
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public final boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathCallback, final FileChooserParams fileChooserParams) {
        if (isFinished()) {
            return true;
        }
        post(new Runnable() {
            @Override
            public void run() {
                final T target = getTarget();
                if (target != null) {
                    final BaseFileChooser<V, P, BaseWebActivity<V, P>> fileChooser = target.createOrGetFileChooser();
                    if (fileChooser != null) {
                        fileChooser.onShowFileChooser(filePathCallback, fileChooserParams);
                    }
                }
            }
        });
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (!isFinished()) {
            try {
                result.cancel();
            } catch (Exception e) {
                //ignore
            }
        }
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        if (consoleMessage != null) {
            if(BuildConfig.DEBUG){
                Log.e(TAG, consoleMessage.messageLevel().name() + " : " + consoleMessage.message());
            }
        }
        return super.onConsoleMessage(consoleMessage);
    }

    private void addWebChromeListener(WebCallback.WebChromeListener chromeListener) {
        this.mWebChromeCallback = new WeakReference<>(chromeListener);
    }

//    private void overrideHistoryStack(WebView view) {
//        final WebView.HitTestResult result = view.getHitTestResult();
//        if (result != null) {
//            if (result.getExtra() != null) {
//                mUrlHash.add(view.getUrl().hashCode());
//            }
//        }
//    }

    public final void attachTarget(T target) {
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
        addWebChromeListener(target);
    }
}
