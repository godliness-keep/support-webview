package com.longrise.android.permission;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

/**
 * Created by godliness on 2020/10/12.
 *
 * @author godliness
 */
abstract class BaseRequestPermissionListener {

    abstract void onPermissionResultInternal(@NonNull String[] permissions, @NonNull int[] grantResults);

    abstract void attachHost(@NonNull FragmentActivity host);
}
