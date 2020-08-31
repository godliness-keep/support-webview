package com.longrise.android.web;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.longrise.android.mvp.internal.activitytheme.ActivityTheme;
import com.longrise.android.mvp.internal.activitytheme.base.BaseActivityTheme;
import com.longrise.android.mvp.internal.loadstyle.LoadingStyleManager;
import com.longrise.android.mvp.internal.loadstyle.base.ILoadStyleListener;
import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
import com.longrise.android.web.internal.BaseWebView;
import com.longrise.android.web.internal.IWebThemeListener;
import com.longrise.android.web.internal.theme.WebTheme;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 * Standard style web-page activities
 */
public abstract class BaseWebThemeActivity<V extends BaseView, P extends BasePresenter<V>> extends BaseWebActivity<V, P> implements IWebThemeListener, ILoadStyleListener {

    private WebTheme<V, P> mWebTheme;
    private BaseWebView mWebView;

    private boolean mErrorState;

    /**
     * Return the back picture button resource {@link ActivityTheme}
     *
     * @return Drawable resource
     */
    @DrawableRes
    protected int overrideBackIcon() {
        return BaseActivityTheme.NONE;
    }

    /**
     * Returns the close button image resource
     *
     * @return Drawable resource
     */
    @DrawableRes
    protected int overrideCloseIcon() {
        return BaseActivityTheme.NONE;
    }

    /**
     * Return the right picture button resource {@link ActivityTheme}
     *
     * @return Drawable resource
     */
    @DrawableRes
    protected int overrideRightIcon() {
        return BaseActivityTheme.NONE;
    }

    /**
     * click right icon button on ActivityTheme {@link ActivityTheme}
     */
    @Override
    public void themeRightTextClick() {
        Toast.makeText(this, "please override themeRightTextClick() method", Toast.LENGTH_SHORT).show();
    }

    /**
     * Return to the right text button resource {@link ActivityTheme}
     *
     * @return String resource
     */
    @StringRes
    protected int overrideRightText() {
        return BaseActivityTheme.NONE;
    }

    /**
     * click right text button on ActivityTheme {@link ActivityTheme}
     */
    @Override
    public void themeRightIconClick() {
        Toast.makeText(this, "please override themeRightIconClick() method", Toast.LENGTH_SHORT).show();
    }

    /**
     * todo 这里不允许在重写
     */
//    @Override
//    protected final void onCreate(@Nullable Bundle savedInstanceState) {
//        createWebTheme();
//        super.onCreate(savedInstanceState);
//    }

    @Override
    public final void setContentView(int layoutResID) {
        if (mWebTheme != null) {
//            mWebTheme.bindThemeView(this, layoutResID);
            mWebTheme.bindThemeView(this, getWebView());
        }
    }

    @Override
    public final BaseWebView getWebView() {
        if (mWebView == null) {
            mWebView = BaseWebView.createOrGetWebView(this);
        }
        return mWebView;
    }

    @Override
    protected boolean webViewGoBack(boolean finish) {
        boolean canGoBack = super.webViewGoBack(finish);
        if (canGoBack) {
            if (mWebTheme != null) {
                mWebTheme.updateCloseButton();
            }
        }
        return canGoBack;
    }

    @Override
    public final void themeBackClick() {
        webViewGoBack(true);
    }

    @Override
    public final void themeCloseClick() {
        finish();
    }

    @Override
    public final void onReceivedTitle(String title) {
        if (mWebTheme != null) {
            mWebTheme.updatePageTitle(title);
        }
    }

    @Override
    public final void onProgressChanged(int newProgress) {
        if (mWebTheme != null) {
            mWebTheme.updateLoadingProgress(newProgress);
        }
    }

    @Override
    public final void loadSucceed() {
        if (mErrorState) {
            LoadingStyleManager.dismissLoadingStyle(this);
            this.mErrorState = false;
        }
    }

    @Override
    public final void loadFailed() {
        LoadingStyleManager.loadingError(this);
        this.mErrorState = true;
    }

    @Override
    public final FrameLayout returnStyleContent() {
        if (mWebTheme != null) {
            return mWebTheme.getThemeContent();
        }
        return null;
    }

    @Override
    public final void onReload() {
        notifyWebViewReload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebTheme != null) {
            mWebTheme.detachTheme();
        }
    }

    private void createWebTheme() {
        final WebTheme<V, P> webTheme = BaseActivityTheme.findTheme(WebTheme.class);
        if (webTheme != null) {
            webTheme.attachTheme(this);
            webTheme.requestThemeFeature(overrideBackIcon(), overrideCloseIcon(), overrideRightIcon(), getRightText());
        }
        this.mWebTheme = webTheme;
    }

    private String getRightText() {
        final int rightRes = overrideRightText();
        if (rightRes == BaseActivityTheme.NONE) {
            return null;
        }
        return getString(rightRes);
    }
}
