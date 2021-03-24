package com.longrise.android.jssdk.lifecycle;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;


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

    public void addLifecycleListener(OnLifecycleListener callback) {
        synchronized (mLifecycles) {
            mLifecycles.add(callback);
        }
    }

    public void removeLifecycleListener(OnLifecycleListener callback) {
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
            // 由于 Activity 页面生命周期管理机制
            // Page-A -> Page-B 时，Page-A 是 Finish
            // 此时在 Page-B 在 onCreate 时，Page-A 还未 onDestroy
            // 故在 onPaused 时判断如果是 Finish 即卸载该 Page 下所有事件
            dispatchLifecycleListener(activity);
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
//        if (activity.isFinishing()) {
        dispatchLifecycleListener(activity);
//        }
    }

    private void dispatchLifecycleListener(Activity host) {
        final Object[] lifeCycles = collectLifecycleListeners();
        if (lifeCycles != null) {
            for (Object lifecycle : lifeCycles) {
                ((OnLifecycleListener) lifecycle).onActivityFinished(host);
            }
        }
    }

    @Nullable
    private Object[] collectLifecycleListeners() {
        Object[] lifecycles = null;
        synchronized (mLifecycles) {
            if (mLifecycles.size() > 0) {
                lifecycles = mLifecycles.toArray();
            }
        }
        return lifecycles;
    }

    private LifecycleManager() {
        mLifecycles = new ArrayList<>(3);
    }

    private static final class Holder {
        private static final LifecycleManager LIFECYCLE_MANAGER = new LifecycleManager();
    }
}
