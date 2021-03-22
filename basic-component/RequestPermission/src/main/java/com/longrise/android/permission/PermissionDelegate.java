package com.longrise.android.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;


import java.util.TreeMap;

/**
 * Created by godliness on 2020/9/26.
 *
 * @author godliness
 */
public final class PermissionDelegate extends Fragment {

    public static final String KEY = "permission-delegate";
    private static final TreeMap<Integer, OnPermissionResultListener> PERMISSION_LISTENER = new TreeMap<>();
    private Context mHost;

    static PermissionDelegate newInstance() {
        return new PermissionDelegate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mHost = context;
    }

    public void check(@NonNull String[] permissions, @NonNull OnPermissionResultListener calback) {
        if (requestPermission(permissions)) {
            calback.onResult(new PermissionResult(getActivity(), permissions, createGrantResults(permissions.length)));
        } else {
            final int requestCode = getRequestCode();
            PERMISSION_LISTENER.put(requestCode, calback);
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final OnPermissionResultListener result = PERMISSION_LISTENER.remove(requestCode);
        if (result != null) {
            result.onResult(new PermissionResult(getActivity(), permissions, grantResults));
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        PermissionsManager.lifecycleOnDestroy(mHost.hashCode());
        super.onDetach();
    }

    private PermissionDelegate() {

    }

    private int getRequestCode() {
        final int requestCode;
        if (PERMISSION_LISTENER.isEmpty()) {
            requestCode = 1;
        } else {
            requestCode = PERMISSION_LISTENER.lastKey() + 1;
        }
        return requestCode;
    }

    private boolean requestPermission(String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mHost, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private int[] createGrantResults(int length) {
        final int[] grantResults = new int[length];
        for (int i = 0; i < length; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        }
        return grantResults;
    }
}
