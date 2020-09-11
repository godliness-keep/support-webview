package com.longrise.android.webview.demo.x5demo;

import android.content.Intent;
import android.os.Message;

import com.longrise.android.x5web.X5;
import com.longrise.android.x5web.internal.bridge.BaseFileChooser;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 */
public final class DemoFileChooser extends BaseFileChooser<WebX5DemoActivity> {

    private static final String TAG = "DemoFileChooser";

    /**
     * 示例
     * <p>
     * 如何对 FileChooser 做二次扩展
     */

    @Override
    protected boolean dispatchActivityOnResult(int requestCode, int resultCode, Intent data) {
        /**
         * 可以拦截由 {@link android.app.Activity#onActivityResult(int, int, Intent)} 返回的消息
         *
         * */

        // 表示不拦截
        return false;
    }

    /**
     * 在同一个 WebActivity 实例内的所有消息都可以获取到
     */
    @Override
    public boolean onHandleMessage(Message msg) {

        X5.error(TAG, "收到...");

        /**
         * 可以拦截由 {@link android.app.Activity#onActivityResult(int, int, Intent)} 返回的消息
         *
         * */

        // 不拦截
        return false;
    }

}
