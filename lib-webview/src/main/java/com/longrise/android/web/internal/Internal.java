package com.longrise.android.web.internal;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;


import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.internal.bridge.BaseFileChooser;
import com.longrise.android.web.internal.bridge.BaseWebChromeClient;
import com.longrise.android.web.internal.bridge.BaseWebViewClient;

/**
 * Created by godliness on 2020/8/22.
 *
 * @author godliness
 */
public final class Internal {

    @NonNull
    public static <V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> BaseWebChromeClient<T> createIfWebChromeClick(BaseWebChromeClient<T> chromeClient) {
        if (chromeClient == null) {
            return new DefaultChromeClient<V, P, T>();
        }
        return chromeClient;
    }

    @NonNull
    public static <V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> BaseWebViewClient<T> createIfWebviewClient(BaseWebViewClient<T> webViewClient) {
        if (webViewClient == null) {
            return new DefaultWebClient<V, P, T>();
        }
        return webViewClient;
    }

    @NonNull
    public static <V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> BaseFileChooser<T> createIfFileChooser(BaseFileChooser<T> fileChooser) {
        if (fileChooser == null) {
            return new DefaultFileChooser<V, P, T>();
        }
        return fileChooser;
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

    private static final class DefaultChromeClient<V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> extends BaseWebChromeClient<T> {
    }

    private static final class DefaultWebClient<V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> extends BaseWebViewClient<T> {
    }

    private static final class DefaultFileChooser<V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> extends BaseFileChooser<T> {
    }

    private Internal() {
        throw new InstantiationError("WebViewFactory Cannot be initialized");
    }
}
