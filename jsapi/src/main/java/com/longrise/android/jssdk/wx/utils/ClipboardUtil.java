package com.longrise.android.jssdk.wx.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.Nullable;

import com.longrise.android.jssdk.wx.BridgeApi;

/**
 * Created by godliness on 2020/10/15.
 *
 * @author godliness
 * 剪贴板
 */
public final class ClipboardUtil {

    /**
     * 设置系统剪贴板的内容
     */
    public static boolean setClipboardData(String clipData) {
        final ClipboardManager manager = getClipboardManager();
        if (manager != null) {
            final ClipData data = ClipData.newPlainText(null, clipData);
            manager.setPrimaryClip(data);
            return true;
        }
        return false;
    }

    /**
     * 获取系统剪贴板内容
     */
    @Nullable
    public static String getClipboardData() {
        final ClipboardManager manager = getClipboardManager();
        if (manager != null) {
            final ClipData data = manager.getPrimaryClip();
            if (data != null && data.getItemCount() > 0) {
                final CharSequence cs = data.getItemAt(0).getText();
                if (cs == null) {
                    return "";
                }
                return cs.toString();
            }
        }
        return null;
    }

    /**
     * 获取 ClipboardManager
     */
    @Nullable
    public static ClipboardManager getClipboardManager() {
        return (ClipboardManager) BridgeApi.get().getSystemService(Context.CLIPBOARD_SERVICE);
    }
}
