package com.longrise.android.permission;


import android.support.annotation.NonNull;

/**
 * Created by godliness on 2020/9/26.
 *
 * @author godliness
 */
public final class PermissionNode {

    private final PermissionDelegate mDelegate;
    private final OnPermissionResultListener mPermissionCallback;

    PermissionNode(PermissionDelegate delegate, OnPermissionResultListener permissionListener) {
        this.mDelegate = delegate;
        this.mPermissionCallback = permissionListener;
    }

    public void check(@NonNull String permission) {
        check(new String[]{permission});
    }

    public void check(@NonNull String[] permissions) {
        if (mDelegate != null) {
            mDelegate.check(permissions, mPermissionCallback);
        }
    }
}
