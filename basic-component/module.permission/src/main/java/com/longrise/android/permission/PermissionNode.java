package com.longrise.android.permission;

import android.support.annotation.NonNull;

/**
 * Created by godliness on 2020/9/26.
 *
 * @author godliness
 */
public final class PermissionNode {

    private final PermissionDelegate mDelegate;
    private final BaseRequestPermissionListener mPermissionCallback;

    PermissionNode(PermissionDelegate delegate, BaseRequestPermissionListener permissionListener) {
        this.mDelegate = delegate;
        this.mPermissionCallback = permissionListener;
    }

    public void request(@NonNull String permission) {
        request(new String[]{permission});
    }

    public void request(@NonNull String[] permissions) {
        if (mDelegate != null) {
            mDelegate.request(permissions, mPermissionCallback);
        }
    }
}
