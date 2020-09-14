package com.longrise.android.x5web;

import android.app.Application;
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
    private static volatile WebAlerter sAlerter;

    public interface Logger {

        void log(String tag, String log);

        void debug(String tag, String log);

        void warn(String tag, String log);

        void error(String tag, String log);

        void print(Throwable e);
    }

    public interface WebAlerter {

        boolean onConsoleMessage(ConsoleMessage message);

        boolean onJsAlert(Context cxt, String url, String message, JsResult jsResult);

        boolean onJsPrompt(Context cxt, String url, String message, String defaultValue, JsPromptResult result);

        boolean onJsConfirm(Context cxt, String url, String message, JsResult jsResult);
    }

    /**
     * 初始化 X5 内核在您的 {@link Application#onCreate()}
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

    /**
     * 代理 Log 打印
     */
    public static void setLogger(Logger logger) {
        if (sLogger != logger) {
            sLogger = logger;
        }
    }

    /**
     * 代理 WebView alert {@link com.longrise.android.x5web.internal.bridge.BaseWebChromeClient}
     */
    public static void setAlerter(WebAlerter alerter) {
        if (sAlerter != alerter) {
            sAlerter = alerter;
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
        if (sAlerter != null) {
            return sAlerter.onConsoleMessage(message);
        }
        return false;
    }

    public static boolean onJsAlert(Context cxt, String url, String message, JsResult jsResult) {
        if (sAlerter != null) {
            return sAlerter.onJsAlert(cxt, url, message, jsResult);
        }
        return false;
    }

    public static boolean onJsPrompt(Context cxt, String url, String message, String defaultValue, JsPromptResult result) {
        if (sAlerter != null) {
            return sAlerter.onJsPrompt(cxt, url, message, defaultValue, result);
        }
        return false;
    }

    public static boolean onJsConfirm(Context cxt, String url, String message, JsResult jsResult) {
        if (sAlerter != null) {
            return sAlerter.onJsConfirm(cxt, url, message, jsResult);
        }
        return false;
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    static {
        if (isDebug()) {
            setLogger(new Logger() {
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
            });
        }

        setAlerter(new WebAlerter() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage console) {
                if (isDebug()) {
                    return true;
                }
                final String levelName = console.messageLevel().name();
                final String message = console.message() +
                        " source: " +
                        console.sourceId() +
                        " line: " +
                        console.lineNumber();

                switch (levelName) {
                    case "ERROR":
                        X5.error("console", message);
                        return true;

                    case "WARNING":
                        X5.warn("console", message);
                        return true;

                    case "LOG":
                        X5.log("console", message);
                        return true;

                    case "DEBUG":
                    case "TIP":
                        X5.debug("console", message);
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
            public boolean onJsConfirm(Context cxt, String url, String message, JsResult jsResult) {
                return false;
            }

            private AlertDialog.Builder createAlertBuilder(Context cxt, String title, String message) {
                return new AlertDialog.Builder(cxt).setTitle(title).setMessage(message);
            }
        });
    }
}
