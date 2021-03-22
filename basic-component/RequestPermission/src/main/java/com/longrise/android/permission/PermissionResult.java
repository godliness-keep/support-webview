package com.longrise.android.permission;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;


import com.longrise.android.permission.dialog.Builder;
import com.longrise.android.permission.dialog.IPermissionDialog;
import com.longrise.android.permission.dialog.OnPermissionListener;
import com.longrise.android.permission.dialog.PermissionDialog;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Created by godliness on 2021/3/11.
 *
 * @author godliness
 */
public final class PermissionResult implements IResult {

    private final WeakReference<FragmentActivity> mRef;
    private final String[] mPermissions;
    private final int[] mGrantResults;

    PermissionResult(@NonNull FragmentActivity cxt, @NonNull String[] permissions, @NonNull int[] grantResults) {
        this.mRef = new WeakReference<>(cxt);
        this.mPermissions = permissions;
        this.mGrantResults = grantResults;
    }

    @NonNull
    @Override
    public String[] getPermissions() {
        return mPermissions;
    }

    @NonNull
    @Override
    public int[] getGrantResults() {
        return mGrantResults;
    }

    @Override
    public boolean isGranted() {
        return mGrantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean isGranted(int index) {
        if (index < mGrantResults.length) {
            return mGrantResults[index] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    @Override
    public boolean isGranted(String permission) {
        int index = 0;
        for (String item : mPermissions) {
            if (TextUtils.equals(item, permission)) {
                return isGranted(index);
            }
            index++;
        }
        return false;
    }

    @Override
    public boolean isClosed() {
        if (mGrantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return !ActivityCompat.shouldShowRequestPermissionRationale(getHost(), mPermissions[0]);
        }
        return false;
    }

    @Override
    public boolean isClosed(int index) {
        if (index < mGrantResults.length) {
            if (mGrantResults[index] != PackageManager.PERMISSION_GRANTED) {
                return !ActivityCompat.shouldShowRequestPermissionRationale(getHost(), mPermissions[index]);
            }
        }
        return false;
    }

    @Override
    public boolean isClosed(@NonNull String permission) {
        int index = 0;
        for (String item : mPermissions) {
            if (TextUtils.equals(item, permission)) {
                return isClosed(index);
            }
            index++;
        }
        return false;
    }

    @Override
    public void showSettingDialog() {
        showSettingDialog(null);
    }

    @Override
    public void showSettingDialog(@Nullable OnPermissionListener listener) {
        final String[] denied = collectDeniedPermissions();
        final String permissionStr = PermissionMap.getNames(denied, getHost());
        final String appName = getAppName();
        PermissionDialog.of(getHost())
                .setTitle(String.format(getString(R.string.permission_title), permissionStr))
                .setContent(String.format(getString(R.string.permission_content), appName, permissionStr))
                .setGuide(String.format(getString(R.string.permission_guide), appName, permissionStr))
                .create()
                .setOnPermissionListener(listener)
                .show();
    }

    @Override
    public IPermissionDialog<?> createSettingDialog() {
        final String[] denied = collectDeniedPermissions();
        final String permissionStr = PermissionMap.getNames(denied, getHost());
        final String appName = getAppName();
        return PermissionDialog.of(getHost())
                .setTitle(String.format(getString(R.string.permission_title), permissionStr))
                .setContent(String.format(getString(R.string.permission_content), appName, permissionStr))
                .setGuide(String.format(getString(R.string.permission_guide), appName, permissionStr))
                .create();
    }

    @Override
    public Builder buildSettingDialog() {
        return PermissionDialog.of(getHost());
    }

    @Nullable
    private String[] collectDeniedPermissions() {
        LinkedList<String> denied = null;
        for (int index = 0; index < mGrantResults.length; index++) {
            if (mGrantResults[index] != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(getHost(), mPermissions[index])) {
                    if (denied == null) {
                        denied = new LinkedList<>();
                    }
                    denied.add(mPermissions[index]);
                }
            }
        }
        if (denied == null || denied.size() <= 0) {
            return null;
        }
        return denied.toArray(new String[0]);
    }

    private FragmentActivity getHost() {
        return mRef.get();
    }

    private String getString(@StringRes int resId) {
        return getHost().getString(resId);
    }

    private String getAppName() {
        return getString(R.string.app_name);
    }
}
