package com.longrise.android.jssdk.core.bridge;

import android.app.Activity;
import android.os.Build;

import com.longrise.android.jssdk.lifecycle.LifecycleManager;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020-04-24.
 *
 * @author godliness
 */

abstract class BridgeLifecyle<T extends Activity> implements LifecycleManager.OnLifecycleListener {

    private final LifecycleManager mManager;
    private WeakReference<T> mHost;

    protected abstract void onDestroy();

    protected final T getTarget() {
        return mHost.get();
    }

    protected final boolean isFinished() {
        final T target = getTarget();
        if (target == null) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return target.isFinishing() || target.isDestroyed();
        }
        return target.isFinishing();
    }

    @Override
    public final void onActivityFinished(Activity host) {
        if (host == getTarget()) {
            mManager.removeLifecyclelistener(this);
            onDestroy();
        }
    }

    final void attachHost(T host) {
        this.mHost = new WeakReference<>(host);
        this.mManager.registerLifecycle(host);
    }

    BridgeLifecyle() {
        this.mManager = LifecycleManager.getManager();
        this.mManager.addLifecyceListener(this);
    }
}
