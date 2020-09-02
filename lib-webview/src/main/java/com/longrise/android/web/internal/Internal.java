package com.longrise.android.web.internal;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;

import com.longrise.android.jssdk.core.bridge.BaseBridge;
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
    public static <V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> BaseWebChromeClient<V, P, T> createIfWebChromeClick(BaseWebChromeClient<V, P, T> chromeClient) {
        if (chromeClient == null) {
            return new DefaultChromeClient<>();
        }
        return chromeClient;
    }

    @NonNull
    public static <V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> BaseWebViewClient<V, P, T> createIfWebviewClient(BaseWebViewClient<V, P, T> webViewClient) {
        if (webViewClient == null) {
            return new DefaultWebClient<>();
        }
        return webViewClient;
    }

    @NonNull
    public static <V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> BaseFileChooser<V, P, T> createIfFileChooser(BaseFileChooser<V, P, T> fileChooser) {
        if (fileChooser == null) {
            return new DefaultFileChooser<>();
        }
        return fileChooser;
    }

    @NonNull
    public static <V extends BaseView, P extends BasePresenter<V>> BaseBridge<BaseWebActivity<V, P>> createIfBridge(BaseBridge<BaseWebActivity<V, P>> bridge) {
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

    private static final class DefaultChromeClient<V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> extends BaseWebChromeClient<V, P, T> {
    }

    private static final class DefaultWebClient<V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> extends BaseWebViewClient<V, P, T> {
    }

    private static final class DefaultFileChooser<V extends BaseView, P extends BasePresenter<V>, T extends BaseWebActivity<V, P>> extends BaseFileChooser<V, P, T> {
    }

    private static final class DefaultBridge<V extends BaseView, P extends BasePresenter<V>> extends BaseBridge<BaseWebActivity<V, P>> {
    }

    private Internal() {
        throw new InstantiationError("WebViewFactory Cannot be initialized");
    }
}
