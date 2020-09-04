package com.longrise.android.webview.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.internal.BaseWebView;

/**
 * Created by godliness on 2020/9/2.
 *
 * @author godliness
 */
public final class WebDemoActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseWebActivity<V, P> implements View.OnClickListener {

    private static final String TAG = "WebDemoActivity";

    private View mBack;
    private TextView mTitle;
    private ProgressBar mProgress;

    private BaseWebView<V, P> mWebView;

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

        loadUrl("https://tieba.baidu.com/index.html");
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

    @Override
    public void onClick(View v) {
        if (!webViewCanGoBack()) {
            finish();
        }
    }
}
