package com.longrise.android.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020/9/26.
 *
 * @author godliness
 * 辅助权限申请
 */
public abstract class IPermissionHelper implements IRequestPermissionListener {

    private String[] mPermissions;
    private int[] mGrantResults;
    private WeakReference<Activity> mHost;

    /**
     * 权限申请返回
     *
     * @param isGranted 表示申请的第一个权限是否通过
     */
    protected abstract void onResult(boolean isGranted);

    @Override
    public final void onResult(@NonNull String[] permissions, @NonNull int[] grantResults, @NonNull Activity host) {
        this.mPermissions = permissions;
        this.mGrantResults = grantResults;
        this.mHost = new WeakReference<>(host);
        onResult(isGrantResult());
    }

    /**
     * 根据权限数组的下标判断该权限是否已经通过
     *
     * @param index 权限数组的下标，注意只能是当前申请权限组中的权限
     */
    protected final boolean isGrantResult(int index) {
        if (mGrantResults.length > index) {
            return mGrantResults[index] == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    /**
     * 根据权限名称判断该权限是否已经通过
     *
     * @param item 权限名称，注意只能是当前申请权限组中的权限
     */
    protected final boolean isGrantResult(String item) {
        final int length = mPermissions.length;
        for (int i = 0; i < length; i++) {
            if (TextUtils.equals(item, mPermissions[i])) {
                return mGrantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }

    /**
     * 如果权限被拒绝，判断申请的权限素组下标为 0 的是否通过
     */
    protected final boolean isShowRationale() {
        return isShowRationale(0);
    }

    /**
     * 如果权限被拒绝，根据下标判断当前申请权限组中的中的权限是否通过
     *
     * @param index 权限下标，注意只能是当前申请权限组中的权限
     */
    protected final boolean isShowRationale(int index) {
        if (mPermissions.length > index) {
            return ActivityCompat.shouldShowRequestPermissionRationale(mHost.get(), mPermissions[index]);
        }
        return false;
    }

    /**
     * 根据权限名称判断该权限是否已经通过
     *
     * @param permission 权限名称，注意只能是当前申请权限组中的权限
     */
    protected final boolean isShowRationale(@NonNull String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(mHost.get(), permission);
    }

    /**
     * 该权限是否需要手动开启
     *
     * @return true 表示用户决绝并且选中不再提示
     */
    protected final boolean isManuallyOn() {
        if (!isGrantResult()) {
            return !isShowRationale();
        }
        return false;
    }

    /**
     * 根据申请的权限下标判断是否需要手动开启该权限
     *
     * @return true 表示用户决绝并且选中不再提示
     */
    protected final boolean isManuallyOn(int index) {
        if (!isGrantResult(index)) {
            return !isShowRationale(index);
        }
        return false;
    }

    /**
     * 根据申请权限的名称判断是否需要手动开启该权限
     *
     * @return true 表示用户决绝并且选中不再提示
     */
    protected final boolean isManuallyOn(String item) {
        if (!isGrantResult(item)) {
            return !isShowRationale(item);
        }
        return false;
    }

    private boolean isGrantResult() {
        return isGrantResult(0);
    }
}
