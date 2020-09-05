package com.longrise.android.jssdk.gson;

import android.support.annotation.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-23.
 *
 * @author godliness
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class GenericHelper {

    public static Type getTypeOfT(Object obj, int position) {
        return getTypeOfT(obj.getClass(), position);
    }

    @Nullable
    public static Type getTypeOfT(Class<?> clz, int position) {
        final Type type = clz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return ((ParameterizedType) type).getActualTypeArguments()[position];
        }
        return null;
    }

    @Nullable
    public static Class<?> getClassOfT(Object obj, int positon) {
        final Type type = getTypeOfT(obj, positon);
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }

    @Nullable
    public static Class<?> getClassOfT(Class<?> clz, int position) {
        return (Class<?>) getTypeOfT(clz, position);
    }

    @Nullable
    public static Class<?> getRawType(Type type) {
        if (type instanceof ParameterizedType) {
            ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        return null;
    }
}
