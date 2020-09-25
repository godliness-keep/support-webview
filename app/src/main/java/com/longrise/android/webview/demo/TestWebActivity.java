package com.longrise.android.webview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.internal.BaseWebView;

/**
 * Created by godliness on 2020/9/19.
 *
 * @author godliness
 */
public final class TestWebActivity extends BaseWebActivity<TestWebActivity> {

    private BaseWebView mWebView;
    /**
     * Returns the current layout resource id
     *
     * @param state {@link Activity#onCreate(Bundle)}
     * @return The current layout id
     */
    @Override
    protected int getLayoutResourceId(@Nullable Bundle state) {
        return 0;
    }

    /**
     * Here the {@link #findViewById(int)}
     */
    @Override
    protected void initView() {


        loadUrl("https://path?...");
    }

    /**
     * Register event or unregister
     *
     * @param regEvent Current state
     */
    @Override
    protected void regEvent(boolean regEvent) {
    }

    /**
//     * Returns the current WebView instance
//     *
//     * @return {@link BaseWebView}
//     */
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

    }

    /**
     * Current page loading progress
     *
     * @param newProgress progress
     */
    @Override
    public void onProgressChanged(int newProgress) {

    }

    /**
     * Loading the page
     */
    @Override
    public void loadSucceed() {

    }

    /**
     * Page loading failed
     */
    @Override
    public void loadFailed() {

    }
}
