package com.longrise.android.web;

import android.util.Log;

/**
 * Created by godliness on 2020/9/3.
 *
 * @author godliness
 */
public class WebLog {

    public static void log(String tag, String log) {
        if (isDebug()) {
            Log.i(tag, log);
        }
    }

    public static void debug(String tag, String log) {
        if (isDebug()) {
            Log.d(tag, log);
        }
    }

    public static void warn(String tag, String log) {
        if (isDebug()) {
            Log.w(tag, log);
        }
    }

    public static void error(String tag, String log) {
        if (isDebug()) {
            Log.e(tag, log);
        }
    }

    public static void print(Throwable e) {
        if (isDebug()) {
            e.printStackTrace();
        }
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
