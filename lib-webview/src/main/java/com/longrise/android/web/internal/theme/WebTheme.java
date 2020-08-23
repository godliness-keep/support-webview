package com.longrise.android.web.internal.theme;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.longrise.android.mvp.internal.activitytheme.base.BaseActivityTheme;
import com.longrise.android.mvp.internal.mvp.BasePresenter;
import com.longrise.android.mvp.internal.mvp.BaseView;
import com.longrise.android.mvp.utils.MvpLog;
import com.longrise.android.web.BaseWebThemeActivity;
import com.longrise.android.web.R;
import com.longrise.android.web.internal.IWebThemeListener;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
@SuppressWarnings("WeakerAccess")
public final class WebTheme<V extends BaseView, P extends BasePresenter<V>> extends BaseActivityTheme<BaseWebThemeActivity<V, P>> implements View.OnClickListener {

    private static final String TAG = "WebTheme";
    private static final int DEFAULT_LOADING_FINISH = 80;
    private static final int MAX_PROGRESS = 100;

    private View mThemeView;
    private FrameLayout mContentView;

    private ImageButton mIvBack;
    private ImageButton mIvClose;
    private TextView mTvTitle;
    private ProgressBar mProgressBar;

    private View mRightView;
    private ImageButton mIvRight;
    private TextView mTvRight;

    private int mHasCloseRes;
    private int mHasRightIconRes;
    private String mHasRightText;
    private boolean mInflatered;

    private IWebThemeListener mThemeCallback;

    public WebTheme() {
        MvpLog.e(TAG, "new WebTheme");
    }

    public void requestThemeFeature(@DrawableRes int backIconRes, @DrawableRes int closeIconRes, @DrawableRes int hasRightIconRes, String hasRightText) {
        if (backIconRes != BaseActivityTheme.NONE) {
            bindBackIconRes(backIconRes);
        }
        this.mHasCloseRes = closeIconRes;
        this.mHasRightIconRes = hasRightIconRes;
        this.mHasRightText = hasRightText;
    }

    public void updatePageTitle(String title) {
        if (mTvTitle != null) {
            mTvTitle.setText(title);
        }
        if (!mInflatered) {
            if (mHasRightText != null) {
                bindRightText();
            }
            if (mHasRightIconRes != BaseActivityTheme.NONE) {
                bindRightIcon();
            }
            mInflatered = true;
        }
    }

    public void updateLoadingProgress(int newProgress) {
        mProgressBar.setProgress(newProgress);
        if (newProgress >= DEFAULT_LOADING_FINISH) {
            mProgressBar.setProgress(MAX_PROGRESS);
            mProgressBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            }, 200);
        } else {
            if (mProgressBar.getVisibility() != View.VISIBLE) {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    public void updateCloseButton() {
        if (mIvClose == null) {
            final ViewStub vs = mThemeView.findViewById(R.id.vs_close_lib_web);
            mIvClose = (ImageButton) vs.inflate();
            mIvClose.setOnClickListener(this);
        } else {
            mIvClose.setVisibility(View.VISIBLE);
        }
        if (mHasCloseRes != BaseActivityTheme.NONE) {
            mIvClose.setImageResource(mHasCloseRes);
            mHasCloseRes = BaseActivityTheme.NONE;
        }
    }

    /**
     * bind to the target
     *
     * @param target T extends BaseMvpActivity
     */
    @Override
    protected void bindTarget(BaseWebThemeActivity target) {
        this.mThemeCallback = target;
    }

    /**
     * unbind to the target,Beware of memory leaks
     */
    @Override
    protected void unBindTarget() {
        this.mThemeCallback = null;
    }

    /**
     * Create the Theme layout
     *
     * @param inflater  LayoutInflater
     * @param container Theme view in container
     */
    @Override
    protected void createThemeLayout(@NonNull LayoutInflater inflater, @NonNull final ViewGroup container) {
        mThemeView = inflater.inflate(R.layout.lib_web_default_theme, container, false);
        mIvBack = mThemeView.findViewById(R.id.ib_left_lib_web);
        mIvBack.setOnClickListener(this);
        mTvTitle = mThemeView.findViewById(R.id.tv_title_lib_web);
        mProgressBar = mThemeView.findViewById(R.id.pb_lib_web);
        mContentView = mThemeView.findViewById(R.id.content_lib_web);
    }

    /**
     * Returns the current Theme view
     *
     * @return View
     */
    @NonNull
    @Override
    protected View getThemeView() {
        return mThemeView;
    }

    /**
     * Returns the current view container
     *
     * @return Theme Content
     */
    @NonNull
    @Override
    public FrameLayout getThemeContent() {
        return mContentView;
    }

    /**
     * Whether you need reuse
     *
     * @return Boolean
     */
    @Override
    protected boolean hasRecycle() {
        return true;
    }

    @Override
    protected void recycled() {
        initState();
    }

    @Override
    public void onClick(View v) {
        if (mThemeCallback != null) {
            final int id = v.getId();
            if (id == R.id.ib_left_lib_web) {
                mThemeCallback.themeBackClick();
            } else if (id == R.id.ib_close_web_title) {
                mThemeCallback.themeCloseClick();
            } else if (id == R.id.tv_right_lib_mvp) {
                mThemeCallback.themeRightTextClick();
            } else if (id == R.id.iv_right_lib_mvp) {
                mThemeCallback.themeRightIconClick();
            }
        }
    }

    private void bindBackIconRes(int backIconRes) {
        if (mIvBack != null) {
            mIvBack.setImageResource(backIconRes);
        }
    }

    private void bindRightText() {
        if (mTvRight == null) {
            mTvRight = inflaterView(R.id.tv_right_lib_mvp);
            mTvRight.setOnClickListener(this);
        }
        mTvRight.setVisibility(View.VISIBLE);
        mTvRight.setText(mHasRightText);
    }

    private void bindRightIcon() {
        if (mIvRight == null) {
            mIvRight = inflaterView(R.id.iv_right_lib_mvp);
            mIvRight.setOnClickListener(this);
        }
        mIvRight.setVisibility(View.VISIBLE);
        mIvRight.setImageResource(mHasRightIconRes);
    }

    private <T extends View> T inflaterView(@IdRes int resId) {
        if (mRightView == null) {
            final ViewStub vs = mThemeView.findViewById(R.id.vs_right_button_web_title);
            mRightView = vs.inflate();
        }
        return mRightView.findViewById(resId);
    }

    private void initState() {
        if (mIvClose != null) {
            mIvClose.setVisibility(View.GONE);
        }
        if (mIvRight != null) {
            mIvRight.setVisibility(View.GONE);
        }
        if (mTvRight != null) {
            mTvRight.setVisibility(View.GONE);
        }
        if (mTvTitle != null) {
            mTvTitle.setText("");
        }
        this.mInflatered = false;
    }

}
