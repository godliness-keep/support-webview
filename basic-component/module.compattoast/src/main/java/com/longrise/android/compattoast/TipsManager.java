package com.longrise.android.compattoast;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;


/**
 * Created by godliness on 2020/10/14.
 *
 * @author godliness
 * 1、只保留居中的 Toast
 */
public final class TipsManager {

    public static final int INFINITY = -1;

    public static void showToast(Activity host, String title) {
        ToastManager.showToast(host, title);
    }

    public static void showToast(Activity host, String title, int duration) {
        ToastManager.showToast(host, title, duration);
    }

    public static void showToast(Activity host, String title, int duration, String icon) {
        ToastManager.showToast(host, title, duration, getIconResource(icon));
    }

    public static void showLoading(Activity host) {
        ToastManager.showLoading(host);
    }

    public static void showLoading(Activity host, String title) {
        ToastManager.showLoading(host, title);
    }

    public static void hideLoading() {
        ToastManager.hide();
        DialogManager.hide();
    }

    public static void hideToast() {
        ToastManager.hide();
        DialogManager.hide();
    }

    public static void showMaskToast(FragmentActivity host, String title) {
        DialogManager.showToast(host.getSupportFragmentManager(), title);
    }

    public static void showMaskToast(FragmentActivity host, String title, int duration) {
        DialogManager.showToast(host.getSupportFragmentManager(), title, duration, INFINITY);
    }

    public static void showMaskToast(FragmentActivity host, String title, int duration, String icon) {
        DialogManager.showToast(host.getSupportFragmentManager(), title, duration, getIconResource(icon));
    }

    public static void showMaskLoading(FragmentActivity host) {
        DialogManager.showLoading(host.getSupportFragmentManager(), host.getString(R.string.string_toast_loading));
    }

    public static void showMaskLoading(FragmentActivity host, String title) {
        DialogManager.showLoading(host.getSupportFragmentManager(), title);
    }

    private static int getIconResource(String icon) {
        if (TextUtils.equals(icon, "success")) {
            return R.drawable.toast_icon_success;
        } else if (TextUtils.equals(icon, "fail")) {
            return R.drawable.toast_icon_fail;
        }
        return INFINITY;
    }
}
