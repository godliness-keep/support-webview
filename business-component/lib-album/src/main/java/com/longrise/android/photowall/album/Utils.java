package com.longrise.android.photowall.album;

import android.content.Context;
import android.support.annotation.StringRes;

/**
 * Created by godliness on 2020/9/8.
 *
 * @author godliness
 */
final class Utils {

    private static Context sCxt;

    static void register(Context cxt) {
        sCxt = cxt.getApplicationContext();
    }

    static Context getCxt() {
        return sCxt;
    }

    static String getString(@StringRes int strRes) {
        return sCxt.getString(strRes);
    }

    static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    static int dip2px(float dpValue) {
        final float scale = Utils.getCxt().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    static float getDensity() {
        return Utils.getCxt().getResources().getDisplayMetrics().density;
    }
}
