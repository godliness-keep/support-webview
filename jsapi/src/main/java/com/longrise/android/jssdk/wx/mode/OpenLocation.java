package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public final class OpenLocation {

    @Expose
    @SerializedName("latitude")
    private int latitude;

    @Expose
    @SerializedName("longitude")
    private int longitude;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("address")
    private String address;

    @Expose
    @SerializedName("scale")
    private int scale;

    @Expose
    @SerializedName("infoUrl")
    private String infoUrl;

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getScale() {
        return scale;
    }

    public String getInfoUrl() {
        return infoUrl;
    }

    @Override
    public String toString() {
        return "{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", scale=" + scale +
                ", infoUrl='" + infoUrl + '\'' +
                '}';
    }
}
