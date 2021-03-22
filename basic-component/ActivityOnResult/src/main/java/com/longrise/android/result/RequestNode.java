package com.longrise.android.result;

import android.content.Intent;
import android.support.annotation.NonNull;


/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 */
public final class RequestNode {

    private final ResultDelegate mDelegate;
    private final OnActivityResultListener mCallback;

    RequestNode(@NonNull ResultDelegate delegate, @NonNull OnActivityResultListener listener) {
        this.mDelegate = delegate;
        this.mCallback = listener;
    }

    public void to(@NonNull Class<?> target) {
        final Intent intent = new Intent(mDelegate.getActivity(), target);
        to(intent);
    }

    public void to(@NonNull Intent intent) {
        mDelegate.startActivity(intent, mCallback);
    }
}
