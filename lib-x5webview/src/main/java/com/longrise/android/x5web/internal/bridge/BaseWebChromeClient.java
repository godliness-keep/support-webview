package com.longrise.android.x5web.internal.bridge;

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

import com.longrise.android.x5web.X5;
import com.longrise.android.x5web.internal.FileChooser;
import com.longrise.android.x5web.internal.IBridgeAgent;
import com.longrise.android.x5web.internal.OnBridgeListener;
import com.longrise.android.x5web.internal.SchemeConsts;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

import java.lang.ref.WeakReference;


/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public abstract class BaseWebChromeClient<T extends IBridgeAgent<T>> extends WebChromeClient {

    private static final String TAG = "BaseWebChromeClient";

    private Handler mHandler;
    private WeakReference<T> mTarget;
    private OnBridgeListener mClientBridge;
    private FileChooser<?> mFileChooser;

    private boolean mFirstLoad = true;

    /**
     * 获取当前 Activity 实例
     */
    @Nullable
    protected final T getTarget() {
        return mTarget.get();
    }

    /**
     * 判断当前宿主 Activity 是否已经 Finished
     */
    protected final boolean isFinished() {
        final T target = getTarget();
        return target == null || target.isFinishing();
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
                    final FileChooser<?> fileChooser = createOrGetFileChooser();
                    if (fileChooser != null) {
                        fileChooser.openFileChooser(uploadMsg, acceptType, capture);
                    }
                }
            }
        });
    }

    /**
     * For android version >= 5.0  todo 4.4 版本是个 Bug 无回调，需要另行约定
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public final boolean onShowFileChooser(WebView webView, final ValueCallback<Uri[]> filePathCallback, final WebChromeClient.FileChooserParams fileChooserParams) {
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

    /**
     * 警示框 {@link X5#onJsAlert(Context, String, String, JsResult)}
     */
    @Override
    public final boolean onJsAlert(WebView webView, String s, String s1, final JsResult jsResult) {
        if (isFinished()) {
            return true;
        }
        return X5.onJsAlert(getContext(), s, s1, jsResult);
    }

    /**
     * 提示框 {@link X5#onJsPrompt(Context, String, String, String, JsPromptResult)}
     */
    @Override
    public final boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        if (isFinished()) {
            return true;
        }
        return X5.onJsPrompt(getContext(), url, message, defaultValue, result);
    }

    /**
     * 确定框 {@link X5#onJsConfirm(Context, String, String, JsResult)}
     */
    @Override
    public final boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
        if (isFinished()) {
            return true;
        }
        return X5.onJsConfirm(getContext(), s, s1, jsResult);
    }

    /**
     * 响应 JavaScript console 消息 {@link X5#onConsoleMessage(ConsoleMessage)}
     */
    @Override
    public final boolean onConsoleMessage(ConsoleMessage console) {
        // 需要注意，X5 WebView 参数可以多个
        return X5.onConsoleMessage(console);
    }

    public final void invokeClientBridge(OnBridgeListener agent) {
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

    private FileChooser<?> createOrGetFileChooser(){
        if (mFileChooser == null) {
            mFileChooser = new FileChooser<>((T) this);
        }
        return mFileChooser;
    }
}
