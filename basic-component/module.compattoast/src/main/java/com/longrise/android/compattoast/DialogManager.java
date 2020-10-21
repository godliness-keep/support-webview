package com.longrise.android.compattoast;

import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentManager;

import com.longrise.android.compattoast.TipsManager;
import com.longrise.android.compattoast.DialogCompat;

/**
 * Created by godliness on 2020/10/14.
 *
 * @author godliness
 */
final class DialogManager {

    static void showToast(FragmentManager fm, String title) {
        DialogCompat.showToast(fm, title, 2000, TipsManager.INFINITY);
    }

    static void showToast(FragmentManager fm, String title, int duration, @DrawableRes int iconId) {
        DialogCompat.showToast(fm, title, duration, iconId);
    }

    static void showLoading(FragmentManager fm, String title) {
        DialogCompat.showLoading(fm, title);
    }

    static void hide() {
        DialogCompat.hide();
    }

    static boolean isShow() {
        return DialogCompat.isShowing();
    }
}
