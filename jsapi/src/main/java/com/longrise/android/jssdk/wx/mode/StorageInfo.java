package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020/10/15.
 *
 * @author godliness
 */
public final class StorageInfo {

    @Expose
    @SerializedName("keys")
    public String[] keys;

    @Expose
    @SerializedName("currentSize")
    public float currentSize;

    @Expose
    @SerializedName("limitSize")
    public long limitSize;
}
