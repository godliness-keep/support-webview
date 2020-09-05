package com.longrise.android.jssdk.core;

import android.os.Build;
import android.webkit.WebView;

import com.longrise.android.jssdk.Request;
import com.longrise.android.jssdk.Response;

/**
 * Created by godliness on 2020-04-10.
 *
 * @author godliness
 */
@SuppressWarnings({"unused"})
public final class JsCallManager {

    /**
     * 通知JavaScript，调用Native方法结束
     *
     * @param webView  {@link WebView}
     * @param response {@link Response}
     */
    public static void notifyJavaScriptCallNativeFinished(WebView webView, Response response) {
        checkNonNull(webView, "webView");
        checkNonNull(response, "response");

        final StringBuilder script = getScript("onNativeCallFinished");
        script.append("('");
        script.append(response.toJson());
        script.append("')");

        callJavaScriptMethod(webView, script.toString());
    }

    public static void notifyJavaScriptCallNativeFinished(WebView webView, String params) {
        checkNonNull(webView, "webView");

        final StringBuilder script = getScript("onNativeCallFinished");
        script.append("('");
        script.append(params);
        script.append("')");

        callJavaScriptMethod(webView, script.toString());
    }

    /**
     * Native 调用 JavaScript 事件，通过构建一个 Request 请求体完成数据传递
     */
    public static void callJavaScriptEvent(WebView webView, Request request) {
        checkNonNull(webView, "webView");
        checkNonNull(request, "request");

        final StringBuilder script = getScript("callJavaScriptFromNative");
        script.append("('");
        script.append(request.toJson());
        script.append("')");

        callJavaScriptMethod(webView, script.toString());
    }

    /**
     * Native 调用 JavaScript 方法，通过构建一个Request请求体完成数据传递
     *
     * @param webView {@link WebView}
     * @param request {@link Request}
     */
    public static void callJavaScriptMethod(WebView webView, Request request) {
        checkNonNull(webView, "webView");
        checkNonNull(request, "request");

        final String methodName = request.getMethodName();
        final Object params = request.getParams();

        final StringBuilder script = getScript(methodName);
        if (params == null) {
            script.append("()");
        } else {
            script.append("('");
            script.append(request.toJson().replaceAll("(\r\n|\r|\n|\n\r)", "</br>"));
            script.append("')");
        }

        callJavaScriptMethod(webView, script.toString());
    }

    private static void callJavaScriptMethod(final WebView webView, final String script) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript(script, null);
                } else {
                    // 数据上限为4096KB
                    webView.loadUrl(script);
                }
            }
        });
    }

    private static StringBuilder getScript(String method) {
        return new StringBuilder("javascript:").append(method);
    }

    private static void checkNonNull(Object object, String name) {
        if (object == null) {
            throw new NullPointerException(name + " must not be null");
        }
    }
}
