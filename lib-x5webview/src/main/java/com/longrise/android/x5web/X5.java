package com.longrise.android.x5web;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 */
public final class X5 {

    private static volatile Logger sLogger;

    /**
     * 初始化 X5 内核
     */
    public static void init(Context cxt) {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean result) {
                if (result) {
                    // 表示 x5 内核加载成功
                    // 否则加载失败，自动降级为系统内核
                    debug("X5", "X5 kernel load succeed");
                }
            }
        };
        QbSdk.initX5Environment(cxt.getApplicationContext(), cb);
    }

    public static void setLogger(BaseLogger logger) {
        if (sLogger != logger) {
            sLogger = logger;
        }
    }

    public static void log(String tag, String log) {
        if (sLogger != null) {
            sLogger.log(tag, log);
        }
    }

    public static void debug(String tag, String log) {
        if (sLogger != null) {
            sLogger.debug(tag, log);
        }
    }

    public static void warn(String tag, String log) {
        if (sLogger != null) {
            sLogger.warn(tag, log);
        }
    }

    public static void error(String tag, String log) {
        if (sLogger != null) {
            sLogger.error(tag, log);
        }
    }

    public static void print(Throwable e) {
        if (sLogger != null) {
            sLogger.print(e);
        }
    }

    public static boolean onConsoleMessage(@NonNull ConsoleMessage message) {
        if (sLogger != null) {
            return sLogger.onConsoleMessage(message);
        }
        return false;
    }

    public static boolean onJsAlert(Context cxt, String url, String message, JsResult jsResult) {
        if (sLogger != null) {
            return sLogger.onJsAlert(cxt, url, message, jsResult);
        }
        return false;
    }

    public static boolean onJsPrompt(Context cxt, String url, String message, String defaultValue, JsPromptResult result) {
        if (sLogger != null) {
            return sLogger.onJsPrompt(cxt, url, message, defaultValue, result);
        }
        return false;
    }

    public static boolean onJsConfirm(Context cxt, String url, String message, JsResult jsResult) {
        if (sLogger != null) {
            return sLogger.onJsConfirm(cxt, url, message, jsResult);
        }
        return false;
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static abstract class BaseLogger implements Logger {

        @Override
        public void log(String tag, String log) {
            Log.i(tag, log);
        }

        @Override
        public void debug(String tag, String log) {
            Log.d(tag, log);
        }

        @Override
        public void warn(String tag, String log) {
            Log.w(tag, log);
        }

        @Override
        public void error(String tag, String log) {
            Log.e(tag, log);
        }

        @Override
        public void print(Throwable e) {
            e.printStackTrace();
        }
    }

    static {
        if (isDebug()) {
            sLogger = new BaseLogger() {
                @Override
                public boolean onConsoleMessage(ConsoleMessage console) {
                    final String levelName = console.messageLevel().name();
                    final String messages = "lineNumber: " + console.lineNumber() + " " + "sourceId: " + console.sourceId();
                    switch (levelName) {
                        case "ERROR":
                            error(console.message(), messages);
                            return true;

                        case "WARNING":
                            warn(console.message(), messages);
                            return true;

                        case "DEBUG":
                        case "LOG":
                        case "TIP":
                            debug(console.message(), messages);
                            return true;

                        default:
                            return false;
                    }
                }

                @Override
                public boolean onJsAlert(Context cxt, String url, String message, final JsResult jsResult) {
                    createAlertBuilder(cxt, url, message)
                            .setPositiveButton(R.string.lib_x5_string_positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (jsResult != null) {
                                        jsResult.confirm();
                                    }
                                }
                            }).create().show();
                    return true;
                }

                @Override
                public boolean onJsPrompt(Context cxt, String url, String message, String defaultValue, JsPromptResult result) {
                    return false;
                }

                @Override
                public boolean onJsConfirm(Context cxt, String url, String message, final JsResult jsResult) {
                    return false;
                }

                private AlertDialog.Builder createAlertBuilder(Context cxt, String title, String message) {
                    return new AlertDialog.Builder(cxt).setTitle(title).setMessage(message);
                }
            };
        }
    }

    interface Logger {

        void log(String tag, String log);

        void debug(String tag, String log);

        void warn(String tag, String log);

        void error(String tag, String log);

        void print(Throwable e);

        boolean onConsoleMessage(ConsoleMessage message);

        boolean onJsAlert(Context cxt, String url, String message, JsResult jsResult);

        boolean onJsPrompt(Context cxt, String url, String message, String defaultValue, JsPromptResult result);

        boolean onJsConfirm(Context cxt, String url, String message, JsResult jsResult);
    }
}
