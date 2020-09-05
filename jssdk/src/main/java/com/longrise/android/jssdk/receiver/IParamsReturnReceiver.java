package com.longrise.android.jssdk.receiver;

import com.longrise.android.jssdk.Response;
import com.longrise.android.jssdk.gson.GenericHelper;
import com.longrise.android.jssdk.receiver.base.BaseReceiver;

/**
 * Created by godliness on 2020-04-16.
 *
 * @author godliness
 */
public abstract class IParamsReturnReceiver<P, R> extends BaseReceiver<P> {

    public abstract R onEvent(P params);

    @Override
    protected final void onReceive(String params) {
        final R r = onEvent(parseParams(params));
        Response.create(getId())
                .result(r)
                .notify(getTarget());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<P>[] getEventParamsType() {
        return new Class[]{GenericHelper.getRawType(getGenericType())};
    }
}
