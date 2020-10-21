package com.longrise.android.permission;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020/10/12.
 *
 * @author godliness
 */
public abstract class IPermissionHelper extends BaseRequestPermissionListener {

    private WeakReference<FragmentActivity> mHost;
    private String[] mPermissions;
    private int[] mGrantResults;

    protected abstract boolean onPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults);

    protected void onDialogCancel() {

    }

    @StringRes
    protected int onContentDesc() {
        return 0;
    }

    protected final boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(getHost(), permission);
    }

    protected final boolean isGranted() {
        return isGranted(0);
    }

    protected final boolean isGranted(int index) {
        if (mGrantResults.length > index) {
            return mGrantResults[index] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    protected final boolean isGranted(String item) {
        final int length = mPermissions.length;
        for (int i = 0; i < length; i++) {
            if (TextUtils.equals(item, mPermissions[i])) {
                return mGrantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    @Override
    final void onPermissionResultInternal(@NonNull String[] permissions, @NonNull int[] grantResults) {
        this.mPermissions = permissions;
        this.mGrantResults = grantResults;
        if (!onPermissionResult(permissions, grantResults)) {
            collectDeniedPermission(permissions, grantResults);
        }
    }

    @Override
    final void attachHost(@NonNull FragmentActivity host) {
        this.mHost = new WeakReference<>(host);
    }

    private FragmentActivity getHost() {
        return mHost.get();
    }

    private String getString(@StringRes int id) {
        return getHost().getString(id);
    }

    private void showPermissionDialog(String[] keys) {
        final String permissionString = PermissionMap.getNames(keys, getHost());
        final String appName = getString(R.string.app_name);
        PermissionDialog.of(getHost())
                .callback(new PermissionDialog.IPermissionListener() {
                    @Override
                    public void onCancel() {
                        onDialogCancel();
                    }
                }).title(String.format(getString(R.string.string_permission_public_title), permissionString))
                .content(String.format(getString(onContentDesc() == 0 ? R.string.string_permission_content_default : R.string.string_permission_content_custom), appName, permissionString))
                .guide(String.format(getString(R.string.string_permission_guide), appName, permissionString))
                .show();
    }

    private void collectDeniedPermission(String[] permissions, int[] grantResults) {
        ArraySet<String> names = null;
        final int length = grantResults.length;
        for (int i = 0; i < length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            if (shouldShowRequestPermissionRationale(permissions[i])) {
                continue;
            }
            if (names == null) {
                names = new ArraySet<>();
            }
            names.add(permissions[i]);
        }

        if (names == null) {
            return;
        }

        showPermissionDialog(names.toArray(new String[0]));
    }
}
