package com.longrise.android.jssdk_x5.receiver.base;

import com.longrise.android.jssdk_x5.Response;

/**
 * Created by godliness on 2020-04-19.
 *
 * @author godliness
 */
public abstract class ICallbackReceiver<P> extends BaseReceiver<P> {

    protected final <R> void callback(R params) {
        callback(Response.RESULT_OK, "ok", params);
    }

    protected final <R> void callback(int state, String desc, R params) {
        Response.create(getId())
                .state(state)
                .desc(desc)
                .result(params)
                .notify(getTarget());
    }
}
