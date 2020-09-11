package com.longrise.android.webview.demo.x5demo;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.longrise.android.jssdk_x5.receiver.IParamsReceiver;
import com.longrise.android.jssdk_x5.receiver.base.EventName;
import com.longrise.android.webview.demo.R;
import com.longrise.android.webview.demo.mode.Bean;
import com.longrise.android.webview.demo.mode.Params;
import com.longrise.android.x5web.BaseWebActivity;
import com.longrise.android.x5web.X5;
import com.longrise.android.x5web.internal.X5WebView;
import com.longrise.android.x5web.internal.bridge.BaseDownloader;
import com.longrise.android.x5web.internal.bridge.BaseFileChooser;
import com.longrise.android.x5web.internal.bridge.BaseWebBridge;
import com.longrise.android.x5web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.x5web.internal.bridge.BaseWebViewClient;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 * 腾讯 X5-内核 WebView 使用示例
 */
public class WebX5DemoActivity extends BaseWebActivity<WebX5DemoActivity> implements View.OnClickListener {

    private static final String TAG = "WebX5DemoActivity";

    private View mBack;
    private TextView mTitle;
    private ProgressBar mProgress;

    private X5WebView mWebView;

    /**
     * 返回布局资源 id
     */
    @Override
    protected int getLayoutResourceId(@Nullable Bundle state) {
        if (state == null) {
            getExtraData();
        } else {
            onRestoreState(state);
        }
        return R.layout.activity_x5web_demo;
    }

    private void getExtraData() {
        // 获取携带的附加参数
    }

    private void onRestoreState(Bundle state) {
        // 表示 Activity 回收后恢复
        // 配合 onSaveInstanceState 即可
    }

    /**
     * 在这里 {@link #findViewById(int)}
     */
    @Override
    protected void initView() {
        mBack = findViewById(R.id.iv_back);
        mTitle = findViewById(R.id.tv_title);
        mProgress = findViewById(R.id.progress);
        mWebView = findViewById(R.id.x5webview);

        loadUrl("file:///android_asset/main.html");

        mParamsReceiver.alive().lifecycle(this);
    }

    /**
     * 在这里注册相关事件，{@link #onDestroy()} 时自动回调为 false
     */
    @Override
    protected void regEvent(boolean regEvent) {
        mBack.setOnClickListener(regEvent ? this : null);
    }

    /**
     * 返回页面的 WebView 引用
     */
    @Override
    public X5WebView getWebView() {
        return mWebView;
    }

    /**
     * 当前 HTML 页面的标题（注意只能获取 Document 的设置）
     */
    @Override
    public void onReceivedTitle(String title) {
        mTitle.setText(title);
    }

    /**
     * 当前 HTML 加载进度 0 ~ 100
     */
    @Override
    public void onProgressChanged(int newProgress) {
        mProgress.setProgress(newProgress);
        if (newProgress >= 100) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgress.setVisibility(View.GONE);
                }
            }, 300);
        } else {
            mProgress.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 页面加载完毕
     */
    @Override
    public void loadSucceed() {
        Log.e(TAG, "加载结束");
    }

    /**
     * 页面加载失败
     */
    @Override
    public void loadFailed() {
        Log.e(TAG, "加载失败");
//        loadUrl(SchemeConsts.BLANK);
    }

    @Override
    public void onClick(View v) {
        if (!webViewCanGoBack()) {
            finish();
        }
    }

    /***
     * todo ====================== 以下为示例代码 ======================
     *      ====================== 以下为示例代码 ======================
     * */

    @Override
    protected void beforeSetContentView() {
        /**
         * 需要在 setContentView 之前的逻辑
         *
         * 例如设置 Window 属性*/
    }

    @Override
    protected boolean onHandleMessage(Message msg) {
        X5.error(TAG, "收到...");
        /**
         * 拦截通过{@link #getHandler()} 发送的消息
         *
         * 返回 false 表示不拦截
         *
         * 通过该 Handler 发送的消息可以在任意 bridge 下接收到
         *
         * */
        return false;
    }

    @Override
    public boolean shouldOverrideUrlLoading(String url) {
        X5.error(TAG, "URL: " + url);
        /**
         * 拦截 WebView 加载
         * 一般对于一些自定义语义动作例如：bb://...
         * */
        return false;
    }

    @Override
    protected BaseWebViewClient<WebX5DemoActivity> getWebViewClient() {
        /**
         *  示例，如何对 WebViewClient 做二次扩展
         */
        return new DemoWebViewClient();
    }

    @Override
    protected BaseWebChromeClient<WebX5DemoActivity> getWebChromeClient() {
        /**
         *  示例，如何对 WebChromeClient 做二次扩展
         */
        return new DemoWebChromeClient();
    }

    @Override
    protected BaseFileChooser<WebX5DemoActivity> getFileChooser() {
        /**
         *  示例，如何对 FileChooser 做二次扩展
         */
        return new DemoFileChooser();
    }

    @Override
    protected BaseWebBridge<WebX5DemoActivity> getBridge() {
        /**
         *  示例，如何对 Bridge 做二次扩展
         */
        return new DemoBridge();
    }

    @Override
    protected BaseDownloader<WebX5DemoActivity> getDownloadHelper() {
        /**
         *  示例，如何对 Downloader 做二次扩展
         */
        return new DemoDownloader();
    }

    /**
     * 含有参数的接收者
     */
    private final IParamsReceiver<Params> mParamsReceiver = new IParamsReceiver<Params>() {
        @Override
        @EventName("shareFeiji")
        public void onEvent(Params params) {
            Log.e(TAG, "接收到JS传递参数：" + params);
            final Bean[] beans = new Bean[1];
            for (int i = 0; i < 1; i++) {
                final Bean bean = new Bean();
                bean.like = "心，是一个容器，不停地累积，关于你的点点滴滴";
                bean.sex = "boy " + i;
                beans[i] = bean;
            }
            // 如果需要返回，可以选择使用
            callback(beans);
        }
    };

}
