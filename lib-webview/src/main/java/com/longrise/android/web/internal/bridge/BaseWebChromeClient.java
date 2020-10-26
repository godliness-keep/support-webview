package com.longrise.android.web.internal.bridge;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.longrise.android.web.WebLog;
import com.longrise.android.web.internal.FileChooser;
import com.longrise.android.web.internal.IBridgeAgent;
import com.longrise.android.web.internal.IBridgeListener;
import com.longrise.android.web.internal.SchemeConsts;

import java.lang.ref.WeakReference;


/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public abstract class BaseWebChromeClient<T extends IBridgeAgent<T>> extends WebChromeClient {

    private Handler mHandler;
    private WeakReference<T> mTarget;
    private IBridgeListener mClientBridge;
    private FileChooser<?> mFileChooser;

    private boolean mFirstLoad = true;

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

    public boolean onHandleMessage(Message msg) {
        return false;
    }

    protected final void post(Runnable task) {
        postDelayed(task, 0);
    }

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
                    final FileChooser<?> fileChooser = createOrGetFileChooser();
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
                    final FileChooser<?> fileChooser = createOrGetFileChooser();
                    if (fileChooser != null) {
                        fileChooser.onShowFileChooser(filePathCallback, fileChooserParams);
                    }
                }
            }
        });
        return true;
    }

    @Override
    public final boolean onJsAlert(WebView webView, String s, String s1, final JsResult jsResult) {
        if (isFinished()) {
            return true;
        }
        return WebLog.onJsAlert(getContext(), s, s1, jsResult);
    }

    /**
     * 提示框 {@link WebLog#onJsPrompt(Context, String, String, String, JsPromptResult)}
     */
    @Override
    public final boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (isFinished()) {
            return true;
        }
        return WebLog.onJsPrompt(getContext(), url, message, defaultValue, result);
    }

    /**
     * 确定框 {@link WebLog#onJsConfirm(Context, String, String, JsResult)}
     */
    @Override
    public final boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
        if (isFinished()) {
            return true;
        }
        return WebLog.onJsConfirm(getContext(), s, s1, jsResult);
    }

    /**
     * 响应 JavaScript console 消息{@link WebLog#onConsoleMessage(ConsoleMessage)}
     */
    @Override
    public final boolean onConsoleMessage(ConsoleMessage console) {
        // 高版本走这里
        // 需要注意，原生 WebView 参数只能一个
        return WebLog.onConsoleMessage(console);
    }

    /**
     * 响应 JavaScript console 消息{@link WebLog#onConsoleMessage(ConsoleMessage)}
     */
    @Override
    public final void onConsoleMessage(String message, int lineNumber, String sourceID) {
        // 低版本走这里
        // 需要注意，原生 WebView 参数只能一个
        final ConsoleMessage console = new ConsoleMessage(message, sourceID, lineNumber, ConsoleMessage.MessageLevel.DEBUG);
        WebLog.onConsoleMessage(console);
    }

    public final void invokeClientBridge(IBridgeListener agent) {
        this.mClientBridge = agent;
    }

    public final void attachTarget(T target, WebView view) {
        view.setWebChromeClient(this);
        this.mHandler = target.getHandler();
        this.mTarget = new WeakReference<>(target);
    }

    @Nullable
    private Context getContext() {
        final T t = getTarget();
        if (t instanceof Activity) {
            return (Context) t;
        } else if (t instanceof Fragment) {
            final Context cxt = ((Fragment) t).getContext();
            if (cxt == null) {
                return ((Fragment) t).getActivity();
            }
            return cxt;
        }
        return null;
    }

    private FileChooser<?> createOrGetFileChooser() {
        if (mFileChooser == null) {
            mFileChooser = new FileChooser<>(getTarget());
        }
        return mFileChooser;
    }
}
