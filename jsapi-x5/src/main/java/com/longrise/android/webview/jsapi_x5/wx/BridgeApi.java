package com.longrise.android.jssdk.wx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by godliness on 2020/9/15.
 *
 * @author godliness
 */
public final class BridgeApi {

    @SuppressLint("StaticFieldLeak")
    private static Context sCxt;

    public static void register(Context cxt) {
        sCxt = cxt.getApplicationContext();
    }

    public static Context get() {
        return sCxt;
    }

    public static FragmentActivity getCurrentActivity(Object host) {
        final Context cxt = getCurrentContext(host);
        if (cxt instanceof FragmentActivity) {
            return (FragmentActivity) cxt;
        }
        throw new IllegalArgumentException("The bridge`s host must be <? extends FragmentActivity>");
    }

    public static Context getCurrentContext(Object host) {
        if (host instanceof FragmentActivity) {
            return (FragmentActivity) host;
        } else if (host instanceof Fragment) {
            final FragmentActivity current = ((Fragment) host).getActivity();
            if (current == null) {
                return ((Fragment) host).getContext();
            }
            return current;
        }
        throw new IllegalArgumentException("The bridge`s host must be <? extends FragmentActivity> || <? extends android.v4.Fragment>");
    }
}
