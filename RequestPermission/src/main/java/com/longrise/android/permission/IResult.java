package com.longrise.android.permission;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.longrise.android.permission.dialog.Builder;
import com.longrise.android.permission.dialog.IPermissionDialog;
import com.longrise.android.permission.dialog.OnPermissionListener;

/**
 * Created by godliness on 2021/3/11.
 *
 * @author godliness
 */
public interface IResult {

    /**
     * 当前申请的权限列表
     */
    @NonNull
    String[] getPermissions();

    /**
     * 申请的权限返回状态列表
     */
    @NonNull
    int[] getGrantResults();

    /**
     * 判断第一个权限是否被授予
     */
    boolean isGranted();

    /**
     * 根据下标判断权限是否被授予
     */
    boolean isGranted(int index);

    /**
     * 根据权限名判断权限是否被授予
     */
    boolean isGranted(String item);

    /**
     * 权限没有被授予的情况下，判断第一个权限是否已经关闭（拒绝并且勾选不再提示）
     */
    boolean isClosed();

    /**
     * 权限没有被授予的情况下，根据下边判断权限是否已经关闭（拒绝并且勾选不再提示）
     */
    boolean isClosed(int index);

    /**
     * 权限没有被授予的情况下，根据权限名称判断权限是否已经关闭（拒绝并且勾选不再提示）
     */
    boolean isClosed(@NonNull String item);

    /**
     * 创建手动设置对话框
     */
    void showSettingDialog();

    /**
     * 默认为第一个权限创建手动设置对话框
     */
    void showSettingDialog(@Nullable OnPermissionListener listener);

    /**
     * 创建已经含有默认提示内容的对话框，需要调用{@link Builder#show()}
     */
    IPermissionDialog<?> createSettingDialog();

    /**
     * 创建自定义内容对话框，需要调用{@link Builder#show()}
     */
    Builder buildSettingDialog();

}
