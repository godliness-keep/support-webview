package com.longrise.android.jssdk_x5;

import com.longrise.android.jssdk_x5.core.protocol.base.AbsDataProtocol;
import com.longrise.android.jssdk_x5.gson.JsonHelper;
import com.longrise.android.jssdk_x5.gson.ParameterizedTypeImpl;
import com.longrise.android.jssdk_x5.sender.INativeListener;
import com.longrise.android.jssdk_x5.sender.IScriptListener;

/**
 * Created by godliness on 2020-04-29.
 *
 * @author godliness
 */
public class Response extends AbsDataProtocol {

    public static final int RESULT_OK = 1;

    /**
     * @return {@link INativeListener <T>}
     */
    public static <T> INativeListener<T> create(int id) {
        return ResponseNative.createInternal(id);
    }

    public static IScriptListener<String> parseResponse(String json) {
        return JsonHelper.fromJson(json, ParameterizedTypeImpl.getTypeImpl(ResponseScript.class, String.class));
    }

    public static <T>ResponseScript<T> scriptResponse(){
        return ResponseScript.createInternal();
    }

    Response() {
    }
}
