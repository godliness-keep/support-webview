package com.longrise.android.permission;

import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.util.Log;


/**
 * Created by godliness on 2020/9/26.
 *
 * @author godliness
 */
final class PermissionsManager {

    private static final String TAG = "PermissionsManager";

    private final ArrayMap<Integer, RequestPermission> mRequests;

    static RequestPermission findRequestPermission(FragmentActivity host) {
        return Holder.MANAGER.createIfRequestPermission(host);
    }

    static void lifecycleOnDestroy(int host) {
        Holder.MANAGER.removeActivityResult(host);
    }

    private RequestPermission createIfRequestPermission(FragmentActivity host) {
        final RequestPermission result = mRequests.get(host.hashCode());
        if (result == null) {
            return createRequestPermission(host);
        }
        return result;
    }

    private RequestPermission createRequestPermission(FragmentActivity host) {
        final RequestPermission activityResult = new RequestPermission(host);
        mRequests.put(host.hashCode(), activityResult);
        return activityResult;
    }

    private void removeActivityResult(int host) {
        final Object old = mRequests.remove(host);

        if (BuildConfig.DEBUG) {
            if (old != null) {
                Log.e(TAG, "lifecycle: succeed");
            } else {
                Log.e(TAG, "lifecycle: failed");
            }
        }
    }

    private static final class Holder {
        private static final PermissionsManager MANAGER = new PermissionsManager();
    }

    private PermissionsManager() {
        this.mRequests = new ArrayMap<>(5);
    }
}
