package com.longrise.android.webview.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.internal.BaseWebView;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 * 实例通过继承{@link BaseWebActivity} 快速实现 Web 页面加载
 * UI 样式自定义，只需要返回其当前页面的 WebView 实例即可
 */
public class WebDemoActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseWebActivity<V, P> implements View.OnClickListener {

    private static final String TAG = "WebDemoActivity";

    private final String URL = "https://www.jianshu.com/p/84d25f5763f9";

    private View mBack;
    private TextView mTitle;
    private BaseWebView mWebView;

    @Override
    protected int getLayoutResourceId(@Nullable Bundle bundle) {
        return R.layout.activity_main;
    }

    /**
     * Here the {@link #findViewById(int)}
     */
    @Override
    protected void initView() {
        mBack = findViewById(R.id.ib_left_lr_mvp);
        mTitle = findViewById(R.id.tv_title_lr_mvp);
        mWebView = findViewById(R.id.webview);

        loadUrl(URL);
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
    public void onClick(View v) {
        if (v.getId() == R.id.ib_left_lr_mvp) {
            webViewGoBack(true);
        }
    }

    /**
     * Returns the current WebView instance
     *
     * @return {@link BaseWebView}
     */
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
        Log.e(TAG, "title: " + title);
        mTitle.setText(title);
    }

    /**
     * Current page loading progress
     *
     * @param newProgress progress
     */
    @Override
    public void onProgressChanged(int newProgress) {
        Log.e(TAG, "loadProgress: " + newProgress);
    }

    /**
     * Loading the page
     */
    @Override
    public void loadSucceed() {
        Log.e(TAG, "loadSucceed");
    }

    /**
     * Page loading failed
     */
    @Override
    public void loadFailed() {
        Log.e(TAG, "loadFailed");
    }
}