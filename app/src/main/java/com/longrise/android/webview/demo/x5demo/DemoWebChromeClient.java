package com.longrise.android.webview.demo.x5demo;

import android.os.Message;

import com.longrise.android.webview.demo.WebX5DemoActivity;
import com.longrise.android.x5web.X5;
import com.longrise.android.x5web.internal.bridge.BaseWebChromeClient;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 */
public final class DemoWebChromeClient extends BaseWebChromeClient<WebX5DemoActivity> {

    private static final String TAG = "DemoWebChromeClient";

    /**
     * 示例
     *
     * 如何对 WebChromeClient 做二次扩展
     */

    /**
     * 在同一个 WebActivity 实例的所有 msg 都可以获取到
     */
    @Override
    public boolean onHandleMessage(Message msg) {
        X5.error(TAG, "收到...");

        // 表示不拦截
        return false;
    }
}
