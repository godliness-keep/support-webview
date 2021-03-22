package com.longrise.android.result;

import android.app.Activity;
import android.content.Context;
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

    private static final TreeMap<Integer, OnActivityResultListener> RESULT_LISTENER = new TreeMap<>();

    private int mHost;

    static ResultDelegate newInstance() {
        return new ResultDelegate();
    }

    void startActivity(Intent intent, OnActivityResultListener listener) {
        final int requestCode = getRequestCode();
        RESULT_LISTENER.put(requestCode, listener);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Activity host = getActivity();
        if (host != null) {
            this.mHost = host.hashCode();
        }
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
        final OnActivityResultListener listener = RESULT_LISTENER.remove(requestCode);
        if (listener != null) {
            listener.onActivityResult(resultCode, data == null ? new Intent() : data);

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

    private ResultDelegate() {

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
