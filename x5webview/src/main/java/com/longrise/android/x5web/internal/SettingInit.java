package com.longrise.android.x5web.internal;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.longrise.android.x5web.X5;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by godliness on 2019-07-10.
 *
 * @author godliness
 */
final class SettingInit {

    @SuppressLint("SetJavaScriptEnabled")
    static void initSetting(@NonNull WebView view) {

        final WebSettings webSettings = view.getSettings();

        //--------------设置支持JavaScript-------------------
        //支持JS交互，Android4.4之后同时开启了IndexDb缓存
        //在Android 4.3版本调用WebSettings.setJavaScriptEnabled()方法时会调用一下reload方法，
        //同时会回调多次WebChromeClient.onJsPrompt()。
        //如果有业务逻辑依赖于这两个方法，就需要注意判断回调多次是否会带来影响了
        webSettings.setJavaScriptEnabled(true);

        //-------设置自适应屏幕-------
        //将图片调整到适合Webview的大小
        webSettings.setUseWideViewPort(true);
        //缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(true);

        //-------缩放操作-------------------
        //不允许缩放
        webSettings.setSupportZoom(false);
        //隐藏原生缩放控件
        webSettings.setDisplayZoomControls(false);
        //设置内置缩放组件不可以缩放
        webSettings.setBuiltInZoomControls(false);
        //禁用文字缩放
        webSettings.setTextZoom(100);

        //-------其他细节操作------------------
        //根据cache-control决定加载模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        //不保存密码
        webSettings.setSavePassword(false);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //设置编码格式
        webSettings.setDefaultTextEncodingName("UTF-8");

        //--------缓存------------------------
        //开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        //开启 Application Caches
        webSettings.setAppCacheEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCachePath(view.getContext().getDir("webCache", 0).getPath());
        webSettings.setGeolocationDatabasePath(view.getContext().getDir("webGelocation", 0).getPath());
        webSettings.setDatabasePath(view.getContext().getDir("webDatabase", 0).getPath());
        //API Level 18 之后系统自动管理
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            webSettings.setAppCacheMaxSize(1024 * 1024 * 10);
        }

        //-------页面白屏问题--------------
        view.setBackgroundColor(ContextCompat.getColor(view.getContext(), android.R.color.transparent));
        view.setBackgroundResource(android.R.color.white);

        //---------Debug 版本在 4.4 之后开启调试 chrome://inspect ------------
        if (X5.isDebug() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    private SettingInit() {
        throw new InstantiationError("SettingInit cannot be initialized");
    }
}
