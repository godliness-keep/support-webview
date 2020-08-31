package com.longrise.android.web.internal.bridge;

import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

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
public abstract class BaseWebChromeClient<T extends BaseWebActivity> extends WebChromeClient {

    private static final String TAG = "BaseWebChromeClient";

    private Handler mHandler;
    private WeakReference<T> mTarget;
    private WeakReference<WebCallback.WebChromeListener> mWebChromeCallback;

    private boolean mFirstLoad = true;

    public final void attachTarget(T target) {
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
        addWebChromeListener(target);
    }

    protected final boolean isFinished() {
        final boolean isAlive = Internal.activityIsFinished(mTarget.get());
        if (isAlive) {
            mHandler.removeCallbacksAndMessages(null);
        }
        return isAlive;
    }

    protected final void post(Runnable task) {
        postDelayed(task, 0);
    }

    protected final void postDelayed(Runnable task, int delay) {
        if (!isFinished()) {
            mHandler.postDelayed(task, delay);
        }
    }

    @Nullable
    protected final WebCallback.WebChromeListener getCallback() {
        return mWebChromeCallback != null ? mWebChromeCallback.get() : null;
    }

    @Nullable
    protected final T getTarget() {
        return mTarget.get();
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
        MvpLog.e(TAG, "newProgress: " + newProgress);
        if (isFinished()) {
            return;
        }
        mHandler.post(new Runnable() {
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
                    final BaseFileChooser fileChooser = target.createOrGetFileChooser();
                    if (fileChooser != null) {
                        fileChooser.openFileChooser(uploadMsg, acceptType, capture);
                    }
                }
            }
        });
    }

    /**
     * For android version >= 5.0  todo 4.4版本是个bug无回调，需要另行约定
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
                    final BaseFileChooser fileChooser = target.createOrGetFileChooser();
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
            MvpLog.e(TAG, consoleMessage.messageLevel().name() + " : " + consoleMessage.message());
        }
        return super.onConsoleMessage(consoleMessage);
    }

    private void addWebChromeListener(WebCallback.WebChromeListener chromeListener) {
        this.mWebChromeCallback = new WeakReference<>(chromeListener);
    }
}
