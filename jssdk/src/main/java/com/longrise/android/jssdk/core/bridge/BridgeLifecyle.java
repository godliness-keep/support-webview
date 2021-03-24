package com.longrise.android.jssdk.core.bridge;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;


import com.longrise.android.jssdk.lifecycle.LifecycleManager;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020-04-24.
 *
 * @author godliness
 */

abstract class BridgeLifecyle<T> implements LifecycleManager.OnLifecycleListener {

    private final LifecycleManager mManager;
    private WeakReference<T> mHost;

    protected abstract void onDestroy();

    protected final T getTarget() {
        return mHost.get();
    }

    @Override
    public final void onActivityFinished(Activity host) {
        if (host == getTarget()) {
            mManager.removeLifecycleListener(this);
            onDestroy();
        }
    }

    protected final boolean isFinished() {
        final T host = getTarget();
        if (host instanceof Activity) {
            return ((Activity) host).isFinishing();
        } else if (host instanceof Fragment) {
            return ((Fragment) host).isDetached();
        }
        return true;
    }

    final void attachHost(T host) {
        this.mManager.registerLifecycle(getHost(host));
        this.mHost = new WeakReference<>(host);
    }

    BridgeLifecyle() {
        this.mManager = LifecycleManager.getManager();
        this.mManager.addLifecycleListener(this);
    }

    private Context getHost(T host) {
        if (host instanceof Activity) {
            return (Context) host;
        } else if (host instanceof Fragment) {
            Context cxt = ((Fragment) host).getContext();
            if (cxt == null) {
                cxt = ((Fragment) host).getActivity();
            }
            if (cxt != null) {
                return cxt;
            }
        }
        throw new IllegalArgumentException("The host type must be Activity or Fragment(V4)");
    }
}
