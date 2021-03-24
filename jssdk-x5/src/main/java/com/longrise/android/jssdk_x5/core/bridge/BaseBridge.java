package com.longrise.android.jssdk_x5.core.bridge;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.longrise.android.jssdk_x5.BuildConfig;
import com.longrise.android.jssdk_x5.Request;
import com.longrise.android.jssdk_x5.Response;
import com.longrise.android.jssdk_x5.lifecycle.LifecycleManager;
import com.longrise.android.jssdk_x5.sender.IScriptListener;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by godliness on 2020-04-09.
 *
 * @author godliness
 */
public abstract class BaseBridge<T> extends BridgeLifecyle<T> implements Handler.Callback, LifecycleManager.OnLifecycleListener {

    private static final String TAG = "BaseBridge";

    private static final int MSG_NOTIFY_NATIVE = 0;
    private static final int MSG_CALL_NATIVE = 1;
    private static final int MSG_CONFIG = 2;

    private final Handler mHandler;
    private boolean mDebug;

    public BaseBridge() {
        this.mHandler = new Handler(Looper.getMainLooper(), this);
    }

    protected abstract WebView getWebView();

    public final void bindTarget(T host) {
        super.attachHost(host);
    }

    public final String bridgeName() {
        return "lrBridge";
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "onDestroy");
        }
    }

    @JavascriptInterface
    public final void onJavaScriptCallFinished(String message) {
        if (!isFinished()) {
            getMessage(MSG_NOTIFY_NATIVE, Response.parseResponse(message)).sendToTarget();
        }
    }

    @JavascriptInterface
    public final void callNativeFromJavaScript(String message) {
        if (!isFinished()) {
            getMessage(MSG_CALL_NATIVE, Request.parseRequest(message)).sendToTarget();
        }
    }

    @JavascriptInterface
    public final void config(String message) {
        if (!isFinished()) {
            getMessage(MSG_CONFIG, Request.parseRequest(message, Config.class)).sendToTarget();
        }
    }

    @Override
    public final boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_NOTIFY_NATIVE:
                final IScriptListener<String> response = parseObjectFromMessage(msg);
                response.onResult();
                if (mDebug) {
                    showMessage(response.serialize());
                }
                return true;

            case MSG_CALL_NATIVE:
                final Request<String> request = parseObjectFromMessage(msg);
                request.dispatchReceiver(getWebView());
                if (mDebug) {
                    showMessage(request.getParams());
                }
                return true;

            case MSG_CONFIG:
                final Request<Config> config = parseObjectFromMessage(msg);
                this.mDebug = config.getParams().debug;
                return true;

            default:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "unknown msg.what: " + msg.what);
                }
                return false;
        }
    }

    static final class Config {
        @Expose
        @SerializedName("debug")
        private boolean debug;
    }

    private void showMessage(String content) {
        if (!isFinished()) {
            new AlertDialog.Builder((Context) getTarget())
                    .setMessage(content)
                    .setCancelable(true).create().show();
        }
    }

    private Message getMessage(int what, Object obj) {
        final Message message = mHandler.obtainMessage(what);
        message.obj = obj;
        return message;
    }

    private <Obj> Obj parseObjectFromMessage(Message msg) {
        return (Obj) msg.obj;
    }
}
