package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020/10/19.
 *
 * @author godliness
 */
public final class Tips {

    @Expose
    @SerializedName("title")
    public String title;

    @Expose
    @SerializedName("icon")
    public String icon;

    @Expose
    @SerializedName("duration")
    public int duration;

    @Expose
    @SerializedName("mask")
    public boolean mask;

}
