package com.longrise.android.result;

import android.content.Intent;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 */
public final class RequestNode {

    private final ResultDelegate mDelegate;
    private final IActivityOnResultListener mCallback;

    RequestNode(ResultDelegate delegate, IActivityOnResultListener listener) {
        this.mDelegate = delegate;
        this.mCallback = listener;
    }

    public void to(Class<?> target) {
        final Intent intent = new Intent(mDelegate.getActivity(), target);
        to(intent);
    }

    public void to(Intent intent) {
        mDelegate.startActivity(intent, mCallback);
    }
}
