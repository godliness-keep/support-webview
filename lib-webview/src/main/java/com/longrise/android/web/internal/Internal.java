package com.longrise.android.web.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;

import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.internal.bridge.BaseFileChooser;
import com.longrise.android.web.internal.bridge.BaseWebBridge;
import com.longrise.android.web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.web.internal.bridge.BaseWebViewClient;

/**
 * Created by godliness on 2020/8/22.
 *
 * @author godliness
 */
public final class Internal {

    @NonNull
    public static <T extends BaseWebActivity> BaseWebChromeClient<T> createIfWebChromeClick(BaseWebChromeClient<T> chromeClient) {
        if (chromeClient == null) {
            return new DefaultChromeClient<>();
        }
        return chromeClient;
    }

    @NonNull
    public static <T extends BaseWebActivity> BaseWebViewClient<T> createIfWebviewClient(BaseWebViewClient<T> webViewClient) {
        if (webViewClient == null) {
            return new DefaultWebClient<>();
        }
        return webViewClient;
    }

    @NonNull
    public static <T extends BaseWebActivity> BaseFileChooser<T> createIfFileChooser(BaseFileChooser<T> fileChooser) {
        if (fileChooser == null) {
            return new DefaultFileChooser<>();
        }
        return fileChooser;
    }

    @NonNull
    public static <T extends BaseWebActivity> BaseWebBridge<T> createIfBridge(BaseWebBridge<T> bridge) {
        if (bridge == null) {
            return new DefaultBridge<>();
        }
        return bridge;
    }

    public static boolean activityIsFinished(Activity target) {
        if (target == null) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return target.isFinishing() || target.isDestroyed();
        }
        return target.isFinishing();
    }

    private static final class DefaultChromeClient<V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity> extends BaseWebChromeClient<T> {
    }

    private static final class DefaultWebClient<T extends BaseWebActivity> extends BaseWebViewClient<T> {
    }

    private static final class DefaultFileChooser<T extends BaseWebActivity> extends BaseFileChooser<T> {
        @Override
        protected boolean dispatchActivityOnResult(int requestCode, int resultCode, Intent data) {
            return false;
        }
    }

    private static final class DefaultBridge<T extends BaseWebActivity> extends BaseWebBridge<T> {
        @Override
        protected void onDestroy() {
        }
    }

    private Internal() {
        throw new InstantiationError("WebViewFactory Cannot be initialized");
    }
}
