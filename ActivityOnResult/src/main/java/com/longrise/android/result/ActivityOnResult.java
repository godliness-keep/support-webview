package com.longrise.android.result;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 * https://github.com/HurryYU/BestActivityResult
 */
public final class ActivityOnResult {

    private final ResultDelegate mDelegate;

    public static ActivityOnResult from(@NonNull Fragment host) {
        return from(requireNonNull(host.getActivity()));
    }

    public static ActivityOnResult from(@NonNull FragmentActivity host) {
        return ResultsManager.findActivityResult(host);
    }

    public RequestNode onResult(@NonNull OnActivityResultListener resultListener) {
        return new RequestNode(mDelegate, resultListener);
    }

    ActivityOnResult(FragmentActivity host) {
        this.mDelegate = findResultDelegate(host.getSupportFragmentManager());
    }

    private ResultDelegate findResultDelegate(FragmentManager fragmentManager) {
        ResultDelegate resultDelegate = reuseResultDelegate(fragmentManager);
        boolean isNewInstance = resultDelegate == null;
        if (isNewInstance) {
            resultDelegate = ResultDelegate.newInstance();
            fragmentManager
                    .beginTransaction()
                    .add(resultDelegate, ResultDelegate.KEY)
                    .commitNow();
        }
        return resultDelegate;
    }

    private ResultDelegate reuseResultDelegate(FragmentManager fragmentManager) {
        return (ResultDelegate) fragmentManager.findFragmentByTag(ResultDelegate.KEY);
    }

    private static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException("host == null");
        }
        return obj;
    }
}
