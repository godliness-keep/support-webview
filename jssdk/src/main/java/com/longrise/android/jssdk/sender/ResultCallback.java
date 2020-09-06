package com.longrise.android.jssdk.sender;

import com.longrise.android.jssdk.ResponseScript;
import com.longrise.android.jssdk.core.protocol.Result;
import com.longrise.android.jssdk.gson.GenericHelper;
import com.longrise.android.jssdk.sender.base.ICallback;

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
