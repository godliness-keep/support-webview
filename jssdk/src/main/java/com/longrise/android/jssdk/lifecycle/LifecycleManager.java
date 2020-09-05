package com.longrise.android.jssdk.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by godliness on 2020-04-24.
 *
 * @author godliness
 */
public final class LifecycleManager implements Application.ActivityLifecycleCallbacks {

    private volatile Application mApp;
    private final ArrayList<OnLifecycleListener> mLifecycles;

    public interface OnLifecycleListener {

        void onActivityFinished(Activity host);
    }

    public static LifecycleManager getManager() {
        return Holder.LIFECYCLE_MANAGER;
    }

    public void addLifecyceListener(OnLifecycleListener callback) {
        synchronized (mLifecycles) {
            mLifecycles.add(callback);
        }
    }

    public void removeLifecyclelistener(OnLifecycleListener callback) {
        synchronized (mLifecycles) {
            mLifecycles.remove(callback);
        }
    }

    public void registerLifecycle(Context context) {
        if (mApp == null) {
            final Application application = (Application) context.getApplicationContext();
            application.registerActivityLifecycleCallbacks(this);
            this.mApp = application;
        }
    }

    void unregisterLifecycle() {
        if (mApp != null) {
            mApp.unregisterActivityLifecycleCallbacks(this);
            this.mApp = null;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity.isFinishing()) {
            dispatchLifecyleListener(activity);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity.isFinishing()) {
            dispatchLifecyleListener(activity);
        }
    }

    private void dispatchLifecyleListener(Activity host) {
        synchronized (mLifecycles) {
            for (int i = 0; i < mLifecycles.size(); i++) {
                mLifecycles.get(i).onActivityFinished(host);
            }
        }
    }

    private LifecycleManager() {
        mLifecycles = new ArrayList<>(3);
    }

    private static final class Holder {
        private static final LifecycleManager LIFECYCLE_MANAGER = new LifecycleManager();
    }
}
