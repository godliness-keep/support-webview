package com.longrise.android.web;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;


/**
 * Created by godliness on 2019-07-13.
 *
 * @author godliness
 * {@link BaseWebThemeActivity}
 */
@SuppressWarnings("unused")
public final class WebParams implements Parcelable {

    public static final String WEB_PARAMS_EXTRA = "web_params_extra";

    private int mThemeBackIcon;
    private int mThemeCloseIcon;
    private int mThemeRightText;
    private int mThemeRightIcon;
    private String mPath;

    public static WebParams obtain() {
        return new WebParams();
    }

    public String path() {
        return mPath;
    }

    public int themeBackIcon() {
        return mThemeBackIcon;
    }

    public int themeCloseIcon() {
        return mThemeCloseIcon;
    }

    public int themeRightText() {
        return mThemeRightText;
    }

    public int themeRightIcon() {
        return mThemeRightIcon;
    }

    /**
     * {@link BaseWebThemeActivity#loadUrl(String)}
     */
    public WebParams path(String path) {
        if (path == null) {
            throw new NullPointerException("path == null");
        }
        this.mPath = path;
        return this;
    }

    /**
     * {@link BaseWebThemeActivity#overrideBackIcon()}
     */
    public WebParams themeBackIcon(@DrawableRes int themeBackIcon) {
        this.mThemeBackIcon = themeBackIcon;
        return this;
    }

    /**
     * {@link BaseWebThemeActivity#overrideCloseIcon()}
     */
    public WebParams themeCloseIcon(@DrawableRes int themeCloseIcon) {
        this.mThemeCloseIcon = themeCloseIcon;
        return this;
    }

    /**
     * {@link BaseWebThemeActivity#overrideRightText()}
     */
    public WebParams themeRightText(@StringRes int themeRightText) {
        this.mThemeRightText = themeRightText;
        return this;
    }

    /**
     * {@link BaseWebThemeActivity#overrideRightIcon()}
     */
    public WebParams themeRightIcon(@DrawableRes int themeRightIcon) {
        this.mThemeRightIcon = themeRightIcon;
        return this;
    }

    protected WebParams(Parcel in) {
        mThemeBackIcon = in.readInt();
        mThemeCloseIcon = in.readInt();
        mThemeRightText = in.readInt();
        mThemeRightIcon = in.readInt();
        mPath = in.readString();
    }

    public static final Creator<WebParams> CREATOR = new Creator<WebParams>() {
        @Override
        public WebParams createFromParcel(Parcel in) {
            return new WebParams(in);
        }

        @Override
        public WebParams[] newArray(int size) {
            return new WebParams[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mThemeBackIcon);
        dest.writeInt(mThemeCloseIcon);
        dest.writeInt(mThemeRightText);
        dest.writeInt(mThemeRightIcon);
        dest.writeString(mPath);
    }

    private WebParams() {

    }
}
