package com.longrise.android.jssdk_x5.receiver.base;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.longrise.android.jssdk_x5.BuildConfig;
import com.longrise.android.jssdk_x5.Request;
import com.longrise.android.jssdk_x5.lifecycle.LifecycleManager;
import com.tencent.smtt.sdk.WebView;

import java.util.List;

/**
 * Created by godliness on 2020-04-16.
 *
 * @author godliness
 */
public final class ReceiversManager<T extends BaseReceiver> implements LifecycleManager.OnLifecycleListener {

    private static final String TAG = "ReceiversManager";

    private final ArrayMap<Integer, ArrayMap<String, T>> mLifecycles;
    private final ArrayMap<String, Integer> mEventNames;

    @SuppressWarnings("unchecked")
    public static <T extends BaseReceiver> ReceiversManager<T> getManager() {
        return Holder.RECEIVER_MANAGER;
    }

    void registerReceiver(Activity lifecycle, ReceiverAgent agent) {
        createEventMap(lifecycle.hashCode(), agent);
    }

    void registerReceiver(Fragment lifecycle, ReceiverAgent agent) {
        createEventMap(lifecycle.hashCode(), agent);
    }

    public void dispatchReceiver(Request<String> request, WebView webView) {
        final T receiver = findReceiver(request.getEventName());
        if (receiver != null) {
            receiver.notifyReceiver(request, webView);
        }
    }

    @Override
    public void onActivityFinished(Activity host) {
        removeLifecyleReceiversIfNeed(host.hashCode());

        if (host instanceof FragmentActivity) {
            final FragmentManager fm = ((FragmentActivity) host).getSupportFragmentManager();
            final List<Fragment> fragments = fm.getFragments();
            if (fragments.size() > 0) {
                // 防止系统内部动了 fragments 数组
                // 故每次都要 size()
                for (int i = 0; i < fragments.size(); i++) {
                    removeLifecyleReceiversIfNeed(fragments.get(i).hashCode());
                }
            }
        }
    }

    private T findReceiver(String eventName) {
        final Integer host = mEventNames.get(eventName);
        final ArrayMap<String, T> receivers = mLifecycles.get(host);
        if (receivers != null) {
            return receivers.get(eventName);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void createEventMap(int host, ReceiverAgent agent) {
        final String eventName = agent.getEventName();
        final ArrayMap<String, T> receiversMap = createLifecyleReceiversIfNeed(host);
        if (receiversMap.put(eventName, (T) agent.getReceiver()) != null) {
            throw new IllegalStateException("Cannot have the same \"" + eventName + "\" in the " + host);
        }
        if (mEventNames.put(eventName, host) != null) {
            throw new IllegalStateException("Cannot use the same \"" + eventName + "\" for different " + host);
        }
    }

    private ArrayMap<String, T> createLifecyleReceiversIfNeed(int host) {
        ArrayMap<String, T> receivers = mLifecycles.get(host);
        if (receivers == null) {
            mLifecycles.put(host, receivers = new ArrayMap<>(3));
        }
        return receivers;
    }

    private void removeLifecyleReceiversIfNeed(int host) {
        final ArrayMap<String, T> receivers = mLifecycles.remove(host);
        if (receivers != null) {
            final int size = receivers.size();
            for (int i = 0; i < size; i++) {
                mEventNames.remove(receivers.keyAt(i));
            }
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Lifecycle size: " + mLifecycles.size() + " Event size: " + mEventNames.size());
            }
        }
    }

    private ReceiversManager() {
        this.mLifecycles = new ArrayMap<>(5);
        this.mEventNames = new ArrayMap<>(5);
        LifecycleManager.getManager().addLifecyceListener(this);
    }

    private static final class Holder {
        private static final ReceiversManager RECEIVER_MANAGER = new ReceiversManager<>();
    }
}
