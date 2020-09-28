package com.longrise.android.result;

import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import android.util.Log;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 */
final class ResultsManager {

    private static final String TAG = "ResultsManager";

    private final ArrayMap<Integer, ActivityResult> mResults;

    static ActivityResult findActivityResult(FragmentActivity host) {
        return Holder.MANAGER.createIfActivityResult(host);
    }

    static void lifecycleOnDestroy(int host) {
        Holder.MANAGER.removeActivityResult(host);
    }

    private ActivityResult createIfActivityResult(FragmentActivity host) {
        final ActivityResult result = mResults.get(host.hashCode());
        if (result == null) {
            return createActivityResult(host);
        }
        return result;
    }

    private ActivityResult createActivityResult(FragmentActivity host) {
        final ActivityResult activityResult = new ActivityResult(host);
        mResults.put(host.hashCode(), activityResult);
        return activityResult;
    }

    private void removeActivityResult(int host) {
        final Object old = mResults.remove(host);

        if (BuildConfig.DEBUG) {
            if (old != null) {
                Log.e(TAG, "lifecycle: succeed");
            } else {
                Log.e(TAG, "lifecycle: failed");
            }
        }
    }

    private ResultsManager() {
        this.mResults = new ArrayMap<>(5);
    }

    private static class Holder {
        private static final ResultsManager MANAGER = new ResultsManager();
    }
}
