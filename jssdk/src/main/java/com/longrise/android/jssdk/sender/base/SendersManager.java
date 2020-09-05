package com.longrise.android.jssdk.sender.base;


import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;

import com.longrise.android.jssdk.BuildConfig;
import com.longrise.android.jssdk.ResponseScript;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by godliness on 2020-04-20.
 *
 * @author godliness
 */
public final class SendersManager<T extends ICallback> {

    private static final String TAG = "SendersManager";

    private final ArrayMap<Integer, ArrayMap<Integer, T>> mLifecycles;
    private final ArrayMap<Integer, Integer> mIds;

    private final SenderLifecycle mCallbackLifecycle;
    private final AtomicInteger mCounter;

    public static SendersManager getManager() {
        return Holder.CALLBACKS_MANAGER;
    }

    public void callbackFromScript(ResponseScript script) {
        final ICallback callback = removeReceiver(script.getCallbackId());
        if (callback != null) {
            callback.onResponse(script);
        }
    }

    int aliveId() {
        return mCounter.incrementAndGet();
    }

    @SuppressWarnings("unchecked")
    void registerSender(View lifecycle, SenderAgent agent) {
        final int host = lifecycle.hashCode();
        if (!mLifecycles.containsKey(host)) {
            mCallbackLifecycle.registerSenderLifecycle(lifecycle);
        }
        createEventMap(host, agent.getId(), (T) agent.getCallback());
    }

    void onViewDetachedFromWindow(int host) {
        final ArrayMap<Integer, T> callbacksMap = mLifecycles.remove(host);
        if (callbacksMap != null) {
            final int size = callbacksMap.size();
            for (int i = 0; i < size; i++) {
                mIds.remove(callbacksMap.keyAt(i));
            }

            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Lifecycle size: " + mLifecycles.size() + " id size: " + mIds.size());
            }
        }
    }

    private SendersManager() {
        this.mLifecycles = new ArrayMap<>(3);
        this.mIds = new ArrayMap<>(3);
        this.mCallbackLifecycle = new SenderLifecycle(this);
        this.mCounter = new AtomicInteger(-1);
    }

    private static final class Holder {
        private static final SendersManager CALLBACKS_MANAGER = new SendersManager();
    }

    private void createEventMap(int host, int id, T callback) {
        final ArrayMap<Integer, T> receiverMap = createLifecyleCallbacksIfNeed(host);
        if (receiverMap.put(id, callback) != null) {
            throw new IllegalStateException("Cannot have the same \"" + id + "\" in the " + host);
        }
        if (mIds.put(id, host) != null) {
            throw new IllegalStateException("Cannot use the same \"" + id + "\" for different " + host);
        }
    }

    private ArrayMap<Integer, T> createLifecyleCallbacksIfNeed(int host) {
        ArrayMap<Integer, T> receivers = mLifecycles.get(host);
        if (receivers == null) {
            mLifecycles.put(host, receivers = new ArrayMap<>(3));
        }
        return receivers;
    }

    private T removeReceiver(int id) {
        final Integer host = mIds.remove(id);
        final ArrayMap<Integer, T> callbacks = mLifecycles.get(host);
        if (callbacks != null) {
            return callbacks.remove(id);
        }
        return null;
    }
}
