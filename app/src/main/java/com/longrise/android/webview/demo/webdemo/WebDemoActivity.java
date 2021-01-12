package com.longrise.android.webview.demo.webdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.longrise.android.jssdk.receiver.IParamsReceiver;
import com.longrise.android.jssdk.receiver.base.EventName;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.internal.BaseWebView;
import com.longrise.android.webview.demo.R;
import com.longrise.android.webview.demo.mode.Bean;
import com.longrise.android.webview.demo.mode.Params;

/**
 * Created by godliness on 2020/9/2.
 *
 * @author godliness
 * Android 原生 WebView 使用示例
 */
public final class WebDemoActivity extends BaseWebActivity<WebDemoActivity> implements View.OnClickListener {

    private static final String TAG = "WebDemoActivity";

    private View mBack;
    private TextView mTitle;
    private ProgressBar mProgress;

    private BaseWebView mWebView;

    private FrameLayout mWebContent;
    private View mLoadFailedView;

    @Override
    protected int getLayoutResourceId(@Nullable Bundle state) {
        return R.layout.activity_web_demo;
    }

    /**
     * Here the {@link #findViewById(int)}
     * 如果需要对 Window 进行操作 {@link #beforeSetContentView()}
     */
    @Override
    protected void initView() {
        mBack = findViewById(R.id.iv_back);
        mTitle = findViewById(R.id.tv_title);
        mProgress = findViewById(R.id.progress);
        mWebView = findViewById(R.id.webview);

        loadUrl("file:///android_asset/main.html");
        /*这里简单示例在加载出错时处理方式*/
        mWebContent = findViewById(R.id.web_content);
        mLoadFailedView = LayoutInflater.from(this).inflate(R.layout.load_failed, mWebView, false);

        /* 注册事件 */
        mParamsReceiver.alive().lifecycle(this);
    }


    /**
     * Register event or unregister
     *
     * @param regEvent Current state
     */
    @Override
    protected void regEvent(boolean regEvent) {
        mBack.setOnClickListener(regEvent ? this : null);
    }

    @Override
    public BaseWebView getWebView() {
        return mWebView;
    }

    /**
     * Current web page title
     *
     * @param title String
     */
    @Override
    public void onReceivedTitle(String title) {
        mTitle.setText(title);
    }

    /**
     * Current page loading progress
     *
     * @param newProgress progress
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
        Log.e(TAG, "newProgress: " + newProgress);
    }

    /**
     * Loading the page
     */
    @Override
    public void loadSucceed() {
        Log.e(TAG, "loadSucceed");

        final ViewParent vp = mLoadFailedView.getParent();
        if (vp instanceof ViewGroup) {
            ((ViewGroup) vp).removeView(mLoadFailedView);
        }
    }

    /**
     * Page loading failed
     */
    @Override
    public void loadFailed() {
        Log.e(TAG, "loadFailed");

        final ViewParent vp = mLoadFailedView.getParent();
        if (vp == null) {
            mWebContent.addView(mLoadFailedView);
        }
    }

    @Override
    public void onClick(View v) {
        if (!webViewCanGoBack()) {
            finish();
        }
    }


    /**
     * todo 以下仅为示例如何使用 jssdk 快速实现与 JavaScript 与 Java 相互交互
     * */

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
