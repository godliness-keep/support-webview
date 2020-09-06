package com.longrise.android.jssdk.receiver;

import com.longrise.android.jssdk.Response;
import com.longrise.android.jssdk.receiver.base.BaseReceiver;

/**
 * Created by godliness on 2020-04-16.
 *
 * @author godliness
 */
public abstract class IReturnReceiver<R> extends BaseReceiver {

    protected abstract R onEvent();

    @Override
    protected final void onReceive(String request) {
        final R r = onEvent();
        Response.create(getId())
                .result(r)
                .notify(getTarget());
    }

    @Override
    protected final Class<?>[] getEventParamsType() {
        return null;
    }
}
