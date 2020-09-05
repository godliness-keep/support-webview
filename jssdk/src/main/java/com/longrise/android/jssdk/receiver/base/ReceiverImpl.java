package com.longrise.android.jssdk.receiver.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created by godliness on 2020-04-28.
 *
 * @author godliness
 */
public final class ReceiverImpl<T extends BaseReceiver> extends ReceiverAgent<T>{

    private final ReceiversManager mManager;

    private final String eventName;
    private final T receiver;

    ReceiverImpl(String eventName, T receiver) {
        this.eventName = eventName;
        this.receiver = receiver;
        this.mManager = ReceiversManager.getManager();
    }

    @Override
    public void lifecycle(Activity lifecycle) {
        mManager.registerReceiver(lifecycle, this);
    }

    @Override
    public void lifecycle(Fragment lifecycle) {
        mManager.registerReceiver(lifecycle, this);
    }

    @Override
    String getEventName() {
        return eventName;
    }

    @Override
    T getReceiver() {
        return receiver;
    }
}
