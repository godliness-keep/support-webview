package com.longrise.android.web;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.longrise.android.web.internal.BaseWebView;

/**
 * Created by godliness on 2020/9/3.
 *
 * @author godliness
 */
public final class BaseWebThemeActivity extends BaseWebActivity {
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
     * 如果需要对 Window 进行操作 {@link #beforeSetContentView()}
     */
    @Override
    protected void initView() {

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
     * Returns the current WebView instance
     *
     * @return {@link BaseWebView}
     */
    @Override
    public BaseWebView getWebView() {
        return null;
    }

    @Override
    public final void onReceivedTitle(String title) {

    }

    @Override
    public final void onProgressChanged(int newProgress) {

    }

    @Override
    public final void loadSucceed() {

    }

    @Override
    public final void loadFailed() {

    }
}
