package com.longrise.android.permission;

import android.app.Activity;
import android.support.annotation.NonNull;

/**
 * Created by godliness on 2020/9/26.
 *
 * @author godliness
 */
public interface IRequestPermissionListener {

    void onResult(@NonNull String[] permissions, @NonNull int[] grantResults, @NonNull Activity host);

}
