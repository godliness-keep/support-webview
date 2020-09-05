package com.longrise.android.jssdk_x5;

import com.longrise.android.jssdk_x5.core.protocol.Result;
import com.longrise.android.jssdk_x5.sender.IScriptListener;
import com.longrise.android.jssdk_x5.sender.base.SendersManager;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-29.
 *
 * @author godliness
 */
public final class ResponseScript<T> extends Response implements IScriptListener<T> {

    private T result;

    static <T> ResponseScript<T> createInternal() {
        return new ResponseScript<>();
    }

    @Override
    public Result<T> getResult(Type typeOfT) {
        return Result.parseResult((String) result, typeOfT);
    }

    @Override
    public void deserialize(T result) {
        this.result = result;
    }

    @Override
    public T serialize() {
        return result;
    }

    @Override
    public void onResult() {
        SendersManager.getManager().callbackFromScript(this);
    }

    private ResponseScript() {

    }
}
