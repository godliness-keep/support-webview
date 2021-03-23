package com.longrise.android.permission;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by godliness on 2020/9/26.
 *
 * @author godliness
 */
public final class RequestPermission {

    private final PermissionDelegate mDelegate;

    public static RequestPermission of(@NonNull Fragment target) {
        return of(requireNonNull(target.getActivity()));
    }

    public static RequestPermission of(@NonNull FragmentActivity target) {
        return PermissionsManager.findRequestPermission(target);
    }

    public PermissionNode onResult(@NonNull OnPermissionResultListener permissionListener) {
        return new PermissionNode(mDelegate, permissionListener);
    }

    RequestPermission(FragmentActivity host) {
        this.mDelegate = findResultDelegate(host.getSupportFragmentManager());
    }

    private PermissionDelegate findResultDelegate(FragmentManager fragmentManager) {
        PermissionDelegate permissionDelegate = reuseResultDelegate(fragmentManager);
        if (permissionDelegate == null) {
            permissionDelegate = PermissionDelegate.newInstance();
            fragmentManager
                    .beginTransaction()
                    .add(permissionDelegate, PermissionDelegate.KEY)
                    .commitNow();
        }
        return permissionDelegate;
    }

    private PermissionDelegate reuseResultDelegate(FragmentManager fragmentManager) {
        return (PermissionDelegate) fragmentManager.findFragmentByTag(PermissionDelegate.KEY);
    }

    private static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }
}
