package com.longrise.android.permission;


import android.support.annotation.NonNull;

/**
 * Created by godliness on 2021/3/11.
 *
 * @author godliness
 */
public interface OnPermissionResultListener {

    /**
     * 权限请求回调
     *
     * @param result 权限请求结果
     */
    void onResult(@NonNull IResult result);
}
