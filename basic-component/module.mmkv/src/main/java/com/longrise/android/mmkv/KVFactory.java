package com.longrise.android.mmkv;

import android.content.SharedPreferences;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.tencent.mmkv.MMKV;

/**
 * Created by godliness on 2020-07-28.
 *
 * @author godliness
 * https://github.com/Tencent/MMKV/wiki/android_tutorial_cn
 */
final class KVFactory {

    static final String DEFAULT = "default";
    private static final ArrayMap<String, SharedPreferences> CACHES;

    static SharedPreferences getSharedPreferences() {
        synchronized (CACHES) {
            return query(DEFAULT);
        }
    }

    static SharedPreferences getSharedPreferences(String id) {
        synchronized (CACHES) {
            return query(id);
        }
    }

    static SharedPreferences getSharedPreferences(String id, int mode) {
        synchronized (CACHES) {
            return query(id, mode);
        }
    }

    static MMKV getMMKV() {
        return (MMKV) getSharedPreferences();
    }

    static MMKV getMMKV(String id) {
        return (MMKV) getSharedPreferences(id);
    }

    static MMKV getMMKV(String id, int mode) {
        return (MMKV) getSharedPreferences(id, mode);
    }

    private static SharedPreferences query(String id) {
        return query(id, MMKV.SINGLE_PROCESS_MODE);
    }

    private static SharedPreferences query(String id, int mode) {
        final SharedPreferences kv = CACHES.get(id + mode);
        if (kv == null) {
            return create(id, mode);
        }
        return kv;
    }

    private static SharedPreferences create(String id, int mode) {
        final MMKV kv;
        if (TextUtils.equals(id, DEFAULT)) {
            kv = MMKV.defaultMMKV();
        } else {
            kv = MMKV.mmkvWithID(id, mode);
        }
        CACHES.put(id + mode, kv);
        return kv;
    }

    static {
        CACHES = new ArrayMap<>(5);
    }

}
