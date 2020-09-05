package com.longrise.android.jssdk_x5.receiver;

import com.longrise.android.jssdk_x5.receiver.base.ICallbackReceiver;

/**
 * Created by godliness on 2020-04-16.
 *
 * @author godliness
 */
public abstract class IReceiver extends ICallbackReceiver {

    protected abstract void onEvent();

    @Override
    protected final void onReceive(String params) {
        onEvent();
    }

    @Override
    protected final Class<?>[] getEventParamsType() {
        return null;
    }
}
