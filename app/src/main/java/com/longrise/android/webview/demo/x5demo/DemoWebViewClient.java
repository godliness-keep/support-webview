package com.longrise.android.webview.demo.x5demo;

import android.os.Message;

import com.longrise.android.x5web.X5;
import com.longrise.android.x5web.internal.bridge.BaseWebViewClient;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 */
public final class DemoWebViewClient extends BaseWebViewClient<WebX5DemoActivity> {

    private static final String TAG = "DemoWebViewClient";

    /**样例
     *
     * 对 WebViewClient 做二次扩展
     * */

    /**
     * 在同一个 WebActivity 实例的所有 msg 都可以获取到
     */
    @Override
    public boolean onHandleMessage(Message msg) {
        X5.error(TAG, "收到...");
        return false;
    }
}
