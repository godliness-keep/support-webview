package com.longrise.android.webview.demo.x5demo;

import android.os.Message;

import com.longrise.android.webview.demo.WebX5DemoActivity;
import com.longrise.android.x5web.X5;
import com.longrise.android.x5web.internal.bridge.BaseWebBridge;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 *
 * 一般来说，使用 jssdk 后 Bridge 的扩展几率不大
 *
 */
public final class DemoBridge extends BaseWebBridge<WebX5DemoActivity> {

    private static final String TAG = "DemoBridge";

    /**
     * 示例
     * <p>
     * 如果对 Bridge 做二次扩展
     */

    @Override
    public boolean onHandleMessage(Message msg) {
        X5.error(TAG, "收到...");
        /**
         * 获取当前 Activity {@link #getTarget()}
         * */
        return false;
    }

    @Override
    protected void onDestroy() {

    }
}
