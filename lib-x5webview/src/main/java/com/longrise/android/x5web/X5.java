package com.longrise.android.x5web;

import android.content.Context;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 */
public final class X5 {

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
