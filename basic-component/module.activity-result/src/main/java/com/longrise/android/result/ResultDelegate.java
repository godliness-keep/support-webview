package com.longrise.android.result;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.TreeMap;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 */
public final class ResultDelegate extends Fragment {

    public static final String KEY = "result-delegate";
    private static final String TAG = "ResultDelegate";

    private static final TreeMap<Integer, IActivityOnResultListener> RESULT_LISTENER = new TreeMap<>();

    private int mHost;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mHost = activity.hashCode();
    }

    public void startActivity(Intent intent, IActivityOnResultListener listener) {
        final int requestCode = getRequestCode();
        RESULT_LISTENER.put(requestCode, listener);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "startActivityForResult");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IActivityOnResultListener listener = RESULT_LISTENER.remove(requestCode);
        if (listener != null) {
            listener.onActivityResult(resultCode, data);

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "onActivityResult");
            }
        }
    }

    @Override
    public void onDetach() {
        ResultsManager.lifecycleOnDestroy(mHost);
        super.onDetach();
    }

    public ResultDelegate() {

    }

    private int getRequestCode() {
        final int requestCode;
        if (RESULT_LISTENER.isEmpty()) {
            requestCode = 1;
        } else {
            requestCode = RESULT_LISTENER.lastKey() + 1;
        }
        return requestCode;
    }
}
