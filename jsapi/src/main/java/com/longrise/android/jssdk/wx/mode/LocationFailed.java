package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020/9/25.
 *
 * @author godliness
 */
public final class LocationFailed {

    @Expose
    @SerializedName("what")
    public int what;

    @Expose
    @SerializedName("type")
    public int type;

    @Expose
    @SerializedName("desc")
    public String desc;

    @Override
    public String toString() {
        return "LocationFailed{" +
                "what=" + what +
                ", type=" + type +
                ", desc='" + desc + '\'' +
                '}';
    }
}
