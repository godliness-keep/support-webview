package com.longrise.android.permission;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.v4.util.ArrayMap;


/**
 * Created by godliness on 2020/10/9.
 *
 * @author godliness
 * https://www.jianshu.com/p/f280b771622b
 */
final class PermissionMap {

    private static final ArrayMap<String, Integer> NAMES = new ArrayMap<>();

    static {
        NAMES.put(Manifest.permission.READ_CALENDAR, R.string.CALENDAR);
        NAMES.put(Manifest.permission.WRITE_CALENDAR, R.string.CALENDAR);
        NAMES.put(Manifest.permission.CAMERA, R.string.CAMERA);
        NAMES.put(Manifest.permission.READ_CONTACTS, R.string.CONTACTS);
        NAMES.put(Manifest.permission.WRITE_CONTACTS, R.string.CONTACTS);
        NAMES.put(Manifest.permission.GET_ACCOUNTS, R.string.CONTACTS);
        NAMES.put(Manifest.permission.ACCESS_FINE_LOCATION, R.string.LOCATION);
        NAMES.put(Manifest.permission.ACCESS_COARSE_LOCATION, R.string.LOCATION);
        NAMES.put(Manifest.permission.RECORD_AUDIO, R.string.AUDIO);
        NAMES.put(Manifest.permission.READ_PHONE_STATE, R.string.PHONE);
        NAMES.put(Manifest.permission.CALL_PHONE, R.string.PHONE);
        NAMES.put(Manifest.permission.READ_CALL_LOG, R.string.PHONE);
        NAMES.put(Manifest.permission.WRITE_CALL_LOG, R.string.PHONE);
        NAMES.put(Manifest.permission.ADD_VOICEMAIL, R.string.PHONE);
        NAMES.put(Manifest.permission.USE_SIP, R.string.PHONE);
        NAMES.put(Manifest.permission.PROCESS_OUTGOING_CALLS, R.string.PHONE);
        NAMES.put(Manifest.permission.SEND_SMS, R.string.SMS);
        NAMES.put(Manifest.permission.RECEIVE_SMS, R.string.SMS);
        NAMES.put(Manifest.permission.READ_SMS, R.string.SMS);
        NAMES.put(Manifest.permission.RECEIVE_WAP_PUSH, R.string.SMS);
        NAMES.put(Manifest.permission.RECEIVE_MMS, R.string.SMS);
        NAMES.put(Manifest.permission.READ_EXTERNAL_STORAGE, R.string.STORAGE);
        NAMES.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.string.STORAGE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            NAMES.put(Manifest.permission.BODY_SENSORS, R.string.SENSORS);
        }
    }

    static String getName(String key, Context cxt) {
        final Integer string = NAMES.get(key);
        if (string == null) {
            return "";
        }
        return cxt.getString(string);
    }

    static String getNames(String[] keys, Context cxt) {
        if (keys == null || keys.length <= 0) {
            return "";
        }
        final StringBuilder names = new StringBuilder();
        final int length = keys.length;
        for (int i = 0; i < length; i++) {
            final Integer string = NAMES.get(keys[i]);
            if (string == null) {
                // do nothing
            } else if (i == (length - 1)) {
                names.append(cxt.getString(string));
            } else {
                names.append(cxt.getString(string)).append("、");
            }
        }
        return names.toString();
    }
}
