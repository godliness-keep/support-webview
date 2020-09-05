package com.longrise.android.jssdk_x5.sender;

import com.longrise.android.jssdk_x5.core.protocol.Result;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-29.
 *
 * @author godliness
 */
public interface IScriptListener<T> {

    Result<T> getResult(Type typeOfT);

    void deserialize(T deserialize);

    T serialize();

    void onResult();
}
