package com.longrise.android.mmkv;

import android.content.Context;
import android.content.SharedPreferences;

import com.tencent.mmkv.MMKV;

/**
 * Created by godliness on 2020-07-28.
 *
 * @author godliness
 * MMKV 实例管理
 */
public final class KVManager {

    public static void initialize(Context cxt) {
        MMKV.initialize(cxt);
    }

    public static void initialize(String rootDir) {
        MMKV.initialize(rootDir);
    }

    /**
     * 获取单进程的 SharePreferences
     *
     * @param id 指定文件 id
     */
    public static SharedPreferences getSharedPreferences(String id) {
        return KVFactory.getSharedPreferences(id);
    }

    /**
     * 获取跨进程默认的 SharePreferences
     */
    public static SharedPreferences getMultiSharedPreferences() {
        return KVFactory.getSharedPreferences(KVFactory.DEFAULT, MMKV.MULTI_PROCESS_MODE);
    }

    /**
     * 获取跨进程的 SharePreferences
     *
     * @param id 指定文件 id
     */
    public static SharedPreferences getMultiSharedPreferences(String id) {
        return KVFactory.getSharedPreferences(id, MMKV.MULTI_PROCESS_MODE);
    }

    private KVManager() {

    }
}
