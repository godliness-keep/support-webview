package com.longrise.android.mmkv;

import android.content.SharedPreferences;
import android.os.Parcelable;

import com.tencent.mmkv.MMKV;

import java.util.Set;

/**
 * Created by godliness on 2020-07-28.
 *
 * @author godliness
 * Default K-V cache
 */
public final class KV {

    private static final MMKV KV = KVFactory.getMMKV();

    /**
     * 获取默认 SharePreferences（基于 MMKV）
     */
    public static SharedPreferences getSharedPreferences() {
        return KV;
    }

    /**
     * 获取默认 MMKV
     */
    public static MMKV getMMKV() {
        return KV;
    }

    /**
     * 通过该函数可以较为方便的将 SharePreferences 迁移至 MMKV
     */
    public static int importFromSharedPreferences(SharedPreferences sharedPreferences) {
        return KV.importFromSharedPreferences(sharedPreferences);
    }

    public static SharedPreferences.Editor putBoolean(String key, boolean value) {
        return KV.putBoolean(key, value);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return KV.getBoolean(key, defValue);
    }

    public static SharedPreferences.Editor putInt(String key, int value) {
        return KV.putInt(key, value);
    }

    public static int getInt(String key, int defValue) {
        return KV.getInt(key, defValue);
    }

    public static SharedPreferences.Editor putLong(String key, long value) {
        return KV.putLong(key, value);
    }

    public static long getLong(String key, long defValue) {
        return KV.getLong(key, defValue);
    }

    public static SharedPreferences.Editor putFloat(String key, float value) {
        return KV.putFloat(key, value);
    }

    public static float getFloat(String key, float defValue) {
        return KV.getFloat(key, defValue);
    }

    public static boolean putDouble(String key, double value) {
        return KV.encode(key, value);
    }

    public static double getDouble(String key) {
        return KV.decodeDouble(key);
    }

    public static SharedPreferences.Editor putBytes(String key, byte[] value) {
        return KV.putBytes(key, value);
    }

    public static byte[] getBytes(String key, byte[] defValue) {
        return KV.getBytes(key, defValue);
    }

    public static SharedPreferences.Editor putString(String key, String value) {
        return KVFactory.getMMKV().putString(key, value);
    }

    public static String getString(String key, String defValue) {
        return KV.getString(key, defValue);
    }

    public static SharedPreferences.Editor putStringSet(String key, Set<String> value) {
        return KV.putStringSet(key, value);
    }

    public static Set<String> getStringSet(String key, Set<String> defValue) {
        return KV.getStringSet(key, defValue);
    }

    public static boolean putParcelable(String key, Parcelable parcelable) {
        return KV.encode(key, parcelable);
    }

    public static <T extends Parcelable> T getParcelable(String key, Class<T> clz) {
        return KV.decodeParcelable(key, clz);
    }

    public static SharedPreferences.Editor remove(String key) {
        return KV.remove(key);
    }

    public static void removeValueForKey(String key) {
        KV.removeValueForKey(key);
    }

    public static void removeValuesForKeys(String[] keys) {
        KV.removeValuesForKeys(keys);
    }

    public static SharedPreferences.Editor clear() {
        return KV.clear();
    }

    public static String[] allKeys() {
        return KV.allKeys();
    }

    public static long totalSize(){
        MMKV mmkv = MMKV.defaultMMKV();
        return mmkv.totalSize();
    }

    private KV() {
    }
}
