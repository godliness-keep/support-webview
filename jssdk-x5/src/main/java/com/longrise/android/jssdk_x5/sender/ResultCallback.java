package com.longrise.android.jssdk_x5.sender;

import com.longrise.android.jssdk_x5.ResponseScript;
import com.longrise.android.jssdk_x5.core.protocol.Result;
import com.longrise.android.jssdk_x5.gson.GenericHelper;
import com.longrise.android.jssdk_x5.sender.base.ICallback;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-29.
 *
 * @author godliness
 */
public abstract class ResultCallback<T> implements ICallback {

    /**
     * Receiver value from script
     *
     * @param result {@link Result<T>}
     */
    protected abstract void onReceiveValue(Result<T> result);

    @SuppressWarnings("unchecked")
    @Override
    public final void onResponse(ResponseScript script) {
        onReceiveValue(script.getResult(getType()));
    }

    private Type getType() {
        return GenericHelper.getTypeOfT(this, 0);
    }
}
