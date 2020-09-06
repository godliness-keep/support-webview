package com.longrise.android.jssdk.gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-15.
 *
 * @author godliness
 */
public class ParameterizedTypeImpl implements ParameterizedType {

    private final Type raw;
    private final Type[] args;

    public static ParameterizedTypeImpl getTypeImpl(Type raw, Type arg) {
        return new ParameterizedTypeImpl(raw, arg);
    }

    public static ParameterizedTypeImpl getTypeImpl(Type raw, Type[] args) {
        return new ParameterizedTypeImpl(raw, args);
    }

    public ParameterizedTypeImpl(Type raw, Type[] args) {
        this.raw = raw;
        this.args = args != null ? args : new Type[0];
    }

    public ParameterizedTypeImpl(Type raw, Type arg) {
        this.raw = raw;
        this.args = new Type[]{arg};
    }

    @Override
    public Type[] getActualTypeArguments() {
        return args;
    }

    @Override
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

}
