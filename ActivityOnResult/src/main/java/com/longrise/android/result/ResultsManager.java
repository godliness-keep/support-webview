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

    private final ArrayMap<Integer, ActivityOnResult> mResults;

    static ActivityOnResult findActivityResult(FragmentActivity host) {
        return Holder.MANAGER.createIfActivityResult(host);
    }

    static void lifecycleOnDestroy(int host) {
        Holder.MANAGER.removeActivityResult(host);
    }

    private ActivityOnResult createIfActivityResult(FragmentActivity host) {
        final ActivityOnResult result = mResults.get(host.hashCode());
        if (result == null) {
            return createActivityResult(host);
        }
        return result;
    }

    private ActivityOnResult createActivityResult(FragmentActivity host) {
        final ActivityOnResult activityOnResult = new ActivityOnResult(host);
        mResults.put(host.hashCode(), activityOnResult);
        return activityOnResult;
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
