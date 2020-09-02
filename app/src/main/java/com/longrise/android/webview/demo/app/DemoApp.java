package com.longrise.android.webview.demo.app;

import android.app.Application;

import com.longrise.android.x5web.X5;

/**
 * Created by godliness on 2020/9/1.
 *
 * @author godliness
 */
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        X5.init(this);
    }
}
