package com.longrise.android.jssdk.wx.utils;

import android.support.annotation.StringRes;

import com.longrise.android.jssdk.wx.BridgeApi;
import com.longrise.android.jssdk.wx.R;

/**
 * Created by godliness on 2020/9/15.
 *
 * @author godliness
 */
public final class ResUtil {

    /**
     * 获取资源下 String
     */
    public static String getString(@StringRes int id) {
        return BridgeApi.get().getString(id);
    }

    /**
     * 获取权限标题提示
     */
    public static String getPermissionTitle(@StringRes int permission) {
        return String.format(ResUtil.getString(R.string.string_permission_public_title), getString(permission));
    }

    /**
     * 获取权限内容提示-设置
     */
    public static String getPermissionSettingDesc(@StringRes int permission) {
        return String.format(getString(R.string.string_permission_tips), getString(R.string.app_name), getString(permission));
    }
}
