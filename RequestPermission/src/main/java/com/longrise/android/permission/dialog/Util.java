package com.longrise.android.permission.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

/**
 * Created by godliness on 2020/10/9.
 *
 * @author godliness
 */
final class Util {

    static void toAppInfo(Context target) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", target.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", target.getPackageName());
        }
        try {
            target.startActivity(localIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void toSettings(Context target) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        target.startActivity(intent);
    }
}
