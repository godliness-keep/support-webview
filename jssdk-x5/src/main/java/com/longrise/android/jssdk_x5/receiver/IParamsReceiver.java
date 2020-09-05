package com.longrise.android.jssdk_x5.receiver;

import com.longrise.android.jssdk_x5.gson.GenericHelper;
import com.longrise.android.jssdk_x5.receiver.base.ICallbackReceiver;

/**
 * Created by godliness on 2020-04-16.
 *
 * @author godliness
 */
public abstract class IParamsReceiver<P> extends ICallbackReceiver<P> {

    public abstract void onEvent(P params);

    @Override
    protected final void onReceive(String params) {
        onEvent(parseParams(params));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected final Class<P>[] getEventParamsType() {
        return new Class[]{GenericHelper.getRawType(getGenericType())};
    }
}
