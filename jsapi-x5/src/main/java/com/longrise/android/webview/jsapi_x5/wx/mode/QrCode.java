package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020/9/24.
 *
 * @author godliness
 */
public final class QrCode {

    @Expose
    @SerializedName("tip")
    public String tip;

    @Expose
    @SerializedName("barCode")
    public boolean barCode;

    @Expose
    @SerializedName("animTime")
    public int animTime;

    @Expose
    @SerializedName("width")
    public int width;

    @Override
    public String toString() {
        return "QrCode{" +
                "tip='" + tip + '\'' +
                ", barCode=" + barCode +
                ", animTime=" + animTime +
                ", width=" + width +
                '}';
    }
}
