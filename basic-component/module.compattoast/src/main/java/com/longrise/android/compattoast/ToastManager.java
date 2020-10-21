package com.longrise.android.compattoast;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by godliness on 2020/10/16.
 *
 * @author godliness
 */
public final class ToastManager {

    private static Toast sToast;

    public static void showToast(Activity host, String title) {
        showToast(host, title, 2000);
    }

    public static void showToast(Activity host, String title, int duration) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        if (duration <= 0) {
            duration = 2000;
        }
        final TextView tips = getDefaultToastView(host);
        tips.setText(title);
        showToast(host, tips, duration);
    }

    public static void showToast(Activity host, String title, int duration, @DrawableRes int icon) {
        if (icon == TipsManager.INFINITY) {
            showToast(host, title, duration);
            return;
        }
        if (TextUtils.isEmpty(title)) {
            return;
        }
        if (duration <= 0) {
            duration = 2000;
        }
        final View tips = getIconToastView(host);
        ((ImageView) tips.findViewById(R.id.iv_toast)).setImageResource(icon);
        ((TextView) tips.findViewById(R.id.tv_toast)).setText(title);
        showToast(host, tips, duration);
    }

    public static void showToast(Activity host, View view) {
        showToast(host, view, 2000);
    }

    public static void showToast(Activity host, View view, int duration) {
        if (sToast == null) {
            sToast = new Toast();
        }
        sToast.setView(view);
        sToast.setDuration(duration);
        sToast.show(host);
    }

    public static void showLoading(Activity host) {
        showLoading(host, host.getString(R.string.string_toast_loading));
    }

    public static void showLoading(Activity host, String title) {
        final View tips = getLoadingView(host);
        ((TextView) tips.findViewById(R.id.tv_toast)).setText(title);
        showToast(host, tips, -1);
    }

    public static void hide() {
        if (sToast != null) {
            sToast.cancel();
        }
    }

    @NonNull
    private static TextView getDefaultToastView(Context cxt) {
        final View tip = getRecycledView(R.id.tv_toast);
        if (tip == null) {
            return (TextView) View.inflate(cxt, R.layout.toast_layout_default_view, null);
        }
        return (TextView) tip;
    }

    @NonNull
    private static ViewGroup getIconToastView(Context cxt) {
        final View tip = getRecycledView(R.id.toast_icon_view);
        if (tip == null) {
            return (ViewGroup) View.inflate(cxt, R.layout.toast_layout_icon_view, null);
        }
        return (ViewGroup) tip;
    }

    private static ViewGroup getLoadingView(Context cxt) {
        final View tip = getRecycledView(R.id.toast_loading);
        if (tip == null) {
            return (ViewGroup) View.inflate(cxt, R.layout.toast_layout_loading, null);
        }
        return (ViewGroup) tip;
    }

    @Nullable
    private static View getRecycledView(int id) {
        if (sToast != null) {
            final View currentView = sToast.getView();
            if (id == currentView.getId()) {
                return currentView;
            }
        }
        return null;
    }
}
