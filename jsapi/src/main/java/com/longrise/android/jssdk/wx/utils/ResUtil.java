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

    public static String getString(@StringRes int id) {
        return BridgeApi.get().getString(id);
    }

    public static String getPermissionString(@StringRes int id) {
        return String.format(getString(R.string.string_permission_tips), getString(R.string.app_name), getString(id));
    }
}
