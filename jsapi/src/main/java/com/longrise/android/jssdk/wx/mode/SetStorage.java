package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020/10/15.
 *
 * @author godliness
 */
public final class SetStorage {

    public static final int LIMIT_STORAGE_SIZE_KB = 10240;
    public static final int LIMIT_STORAGE_SIZE_B = LIMIT_STORAGE_SIZE_KB * 1024;

    @Expose
    @SerializedName("key")
    public String key;

    @Expose
    @SerializedName("data")
    public Object data;
}
