package com.longrise.android.jssdk.receiver.base;

import com.longrise.android.jssdk.Response;

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
