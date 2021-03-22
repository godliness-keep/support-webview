package com.longrise.android.permission.dialog;


import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

/**
 * Created by godliness on 2021/3/12.
 *
 * @author godliness
 */
public final class Builder {

    boolean mCancelable = true;

    String mTitle;
    String mContent;
    String mGuide;

    private final FragmentActivity mHost;

    Builder(@NonNull FragmentActivity host) {
        this.mHost = host;
    }

    public Builder setTitle(String strRes) {
        this.mTitle = strRes;
        return this;
    }

    public Builder setContent(String strRes) {
        this.mContent = strRes;
        return this;
    }

    public Builder setGuide(String strRes) {
        this.mGuide = strRes;
        return this;
    }

    public Builder setCancelable(boolean cancelable) {
        this.mCancelable = cancelable;
        return this;
    }

    public PermissionDialog create() {
        return new PermissionDialog().create(this, mHost);
    }

    public void show() {
        new PermissionDialog().show(this, mHost);
    }
}
