package com.longrise.android.permission;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

/**
 * Created by godliness on 2020/10/9.
 *
 * @author godliness
 */
final class Test {

    static void main(FragmentActivity host) {
        RequestPermission.of(host)
                .onPermissionResult(new IPermissionHelper() {
                    @Override
                    protected boolean onPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (isGranted()) {
                            // 如果只申请一个权限，可以通过这种方式
                        }

                        if (isGranted(2)) {
                            // 多个权限可以根据下标
                        }

                        if (isGranted(Manifest.permission.CAMERA)) {
                            // 也可以根据权限名称
                        }

                        // 返回值的作用：当用户拒绝权限，并且勾选不再提示，默认由框架内部处理（提供去设置功能）
                        // 否则拦截自定义
                        return false;
                    }

                    @Override
                    protected void onDialogCancel() {
                        // 点击了取消
                    }

                    @Override
                    protected int onContentDesc() {
                        return -1;
                    }
                }).request(Manifest.permission.CAMERA);
    }
}
