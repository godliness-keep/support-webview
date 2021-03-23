package com.longrise.android.permission.dialog;

import android.content.DialogInterface;

/**
 * Created by godliness on 2021/3/13.
 *
 * @author godliness
 */
public interface IPermissionDialog<T extends IPermissionDialog<T>> {

    void show();

    T setOnCancelListener(DialogInterface.OnCancelListener cancelListener);

    T setOnDismissListener(DialogInterface.OnDismissListener dismissListener);

    T setOnPermissionListener(OnPermissionListener permissionListener);
}
