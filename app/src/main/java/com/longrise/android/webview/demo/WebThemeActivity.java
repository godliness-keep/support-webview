package com.longrise.android.webview.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.longrise.android.web.BaseWebThemeActivity;

/**
 * Created by godliness on 2020/8/23.
 *
 * @author godliness
 */
public class WebThemeActivity extends BaseWebThemeActivity {

    @Override
    protected final int getLayoutResourceId(@Nullable Bundle bundle) {
        return R.layout.lib_web_default_theme;
    }

    /**
     * Here the {@link #findViewById(int)}
     */
    @Override
    protected void initView() {
        loadUrl("https://www.jianshu.com/p/84d25f5763f9");
    }

    /**
     * Register event or unregister
     *
     * @param regEvent Current state
     */
    @Override
    protected void regEvent(boolean regEvent) {

    }
}
