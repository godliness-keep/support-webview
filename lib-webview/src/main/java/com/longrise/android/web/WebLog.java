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

    public static boolean onJsAlert(Context target, String s, String s1, JsResult jsResult) {
        if (sLogger != null) {
            return sLogger.onJsAlert(target, s, s1, jsResult);
        }
        return false;
    }

    public static boolean onJsPrompt(Context target, String url, String message, String defaultValue, JsPromptResult result) {
        if (sLogger != null) {
            return sLogger.onJsPrompt(target, url, message, defaultValue, result);
        }
        return false;
    }

    public static boolean onJsConfirm(Context target, String s, String s1, JsResult jsResult) {
        if (sLogger != null) {
            return sLogger.onJsConfirm(target, s, s1, jsResult);
        }
        return false;
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
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

        boolean onJsAlert(Context target, String s, String s1, JsResult jsResult);

        boolean onJsPrompt(Context target, String url, String message, String defaultValue, JsPromptResult result);

        boolean onJsConfirm(Context target, String s, String s1, JsResult jsResult);
    }
}
