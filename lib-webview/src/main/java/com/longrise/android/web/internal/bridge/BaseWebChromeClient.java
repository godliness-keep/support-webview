package com.longrise.android.web.internal.bridge;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.WebLog;
import com.longrise.android.web.internal.ClientBridgeAgent;
import com.longrise.android.web.internal.Internal;
import com.longrise.android.web.internal.SchemeConsts;

import java.lang.ref.WeakReference;


/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public abstract class BaseWebChromeClient<T extends BaseWebActivity<T>> extends WebChromeClient {

    private static final String TAG = "BaseWebChromeClient";

    private Handler mHandler;
    private WeakReference<BaseWebActivity<T>> mTarget;
    private ClientBridgeAgent mClientBridge;

    private boolean mFirstLoad = true;

    /**
     * 获取当前 Activity 实例
     */
    @SuppressWarnings("unchecked")
    @Nullable
    protected final T getTarget() {
        return (T) mTarget.get();
    }

    /**
     * 判断当前宿主 Activity 是否已经 Finished
     */
    protected final boolean isFinished() {
        return Internal.activityIsFinished(mTarget.get());
    }

    /**
     * 获取 Handler 实例
     * 注：每个 Web 页面实例都有且共用一个 Handler 实例
     * 即在 Activity、WebChromeClient、WebViewClient、FileChooser 之间发送的消息可以相互接收到
     */
    protected final Handler getHandler() {
        return mHandler;
    }

    /**
     * 拦截通过 Handler 发送的消息
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

    @Override
    public void onReceivedTitle(WebView view, final String title) {
        if (isFinished()) {
            return;
        }
        if (TextUtils.equals(title, SchemeConsts.BLANK)) {
            return;
        }
        if (mClientBridge != null) {
            mClientBridge.onReceivedTitle(title);
        }
    }

    @Override
    public void onProgressChanged(WebView view, final int newProgress) {
        if (isFinished()) {
            return;
        }

        if (mClientBridge != null) {
            mClientBridge.onProgressChanged(newProgress);
        }

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
                    final BaseFileChooser<T> fileChooser = target.createOrGetFileChooser();
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
                    final BaseFileChooser<T> fileChooser = target.createOrGetFileChooser();
                    if (fileChooser != null) {
                        fileChooser.onShowFileChooser(filePathCallback, fileChooserParams);
                    }
                }
            }
        });
        return true;
    }

    /**
     * 警示框 {@link WebLog#onJsAlert(Context, String, String, JsResult)}
     */
    @Override
    public boolean onJsAlert(WebView webView, String s, String s1, final JsResult jsResult) {
        if (isFinished()) {
            return true;
        }
        return WebLog.onJsAlert(getTarget(), s, s1, jsResult);
    }

    /**
     * 提示框 {@link WebLog#onJsPrompt(Context, String, String, String, JsPromptResult)}
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (isFinished()) {
            return true;
        }
        return WebLog.onJsPrompt(getTarget(), url, message, defaultValue, result);
    }

    /**
     * 确定框 {@link WebLog#onJsConfirm(Context, String, String, JsResult)}
     */
    @Override
    public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
        if (isFinished()) {
            return true;
        }
        return WebLog.onJsConfirm(getTarget(), s, s1, jsResult);
    }

    /**
     * 响应 console {@link WebLog#onConsoleMessage(ConsoleMessage)}
     */
    @Override
    public boolean onConsoleMessage(ConsoleMessage console) {
        return WebLog.onConsoleMessage(console);
    }

    public final void invokeClientBridge(ClientBridgeAgent agent) {
        this.mClientBridge = agent;
    }

    public final void attachTarget(BaseWebActivity<T> target, WebView view) {
        view.setWebChromeClient(this);
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
    }
}
