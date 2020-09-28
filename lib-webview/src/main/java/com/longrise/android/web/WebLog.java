package com.longrise.android.web;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;

/**
 * Created by godliness on 2020/9/3.
 *
 * @author godliness
 */
public class WebLog {

    private static volatile Logger sLogger;
    private static volatile WebAlerter sAlerter;

    /**
     * Log 打印，可以通过 {@link #setLogger(Logger)} 代理
     */
    public interface Logger {

        void log(String tag, String log);

        void debug(String tag, String log);

        void warn(String tag, String log);

        void error(String tag, String log);

        void print(Throwable e);
    }

    /**
     * WebView alert，可以通过{@link #setWebAlerter(WebAlerter)} 代理
     */
    public interface WebAlerter {

        boolean onConsoleMessage(ConsoleMessage message);

        boolean onJsAlert(Context target, String s, String s1, JsResult jsResult);

        boolean onJsPrompt(Context target, String url, String message, String defaultValue, JsPromptResult result);

        boolean onJsConfirm(Context target, String s, String s1, JsResult jsResult);
    }

    public static void setLogger(Logger logger) {
        if (sLogger != logger) {
            sLogger = logger;
        }
    }

    public static void setWebAlerter(WebAlerter alert) {
        if (sAlerter != alert) {
            sAlerter = alert;
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

    public static boolean onJsAlert(Context target, String s, String s1, JsResult jsResult) {
        if (sAlerter != null) {
            return sAlerter.onJsAlert(target, s, s1, jsResult);
        }
        return false;
    }

    public static boolean onJsPrompt(Context target, String url, String message, String defaultValue, JsPromptResult result) {
        if (sAlerter != null) {
            return sAlerter.onJsPrompt(target, url, message, defaultValue, result);
        }
        return false;
    }

    public static boolean onJsConfirm(Context target, String s, String s1, JsResult jsResult) {
        if (sAlerter != null) {
            return sAlerter.onJsConfirm(target, s, s1, jsResult);
        }
        return false;
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    static {
        if (isDebug()) {
            // Debug 模式设置
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

        // 设置 WebView alert
        setWebAlerter(new WebAlerter() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage console) {
                if (!isDebug()) {
                    return true;
                }
                final String levelName = console.messageLevel().name();
                final String message = console.message() +
                        " line: " +
                        console.lineNumber() +
                        " source: " +
                        console.sourceId();

                switch (levelName) {
                    case "ERROR":
                        WebLog.error("console", message);
                        return true;

                    case "WARNING":
                        WebLog.warn("console", message);
                        return true;

                    case "LOG":
                        WebLog.log("console", message);
                        return true;

                    case "DEBUG":
                    case "TIP":
                        WebLog.debug("console", message);
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public boolean onJsAlert(Context cxt, String url, String message, final JsResult jsResult) {
                createAlertBuilder(cxt, url, message)
                        .setPositiveButton(R.string.web_string_positive, new DialogInterface.OnClickListener() {
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
            public boolean onJsPrompt(Context target, String url, String message, String defaultValue, JsPromptResult result) {
                return false;
            }

            @Override
            public boolean onJsConfirm(Context target, String s, String s1, JsResult jsResult) {
                return false;
            }

            private AlertDialog.Builder createAlertBuilder(Context cxt, String title, String message) {
                return new AlertDialog.Builder(cxt).setTitle(title).setMessage(message);
            }
        });
    }
}
