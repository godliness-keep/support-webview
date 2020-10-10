package com.longrise.android.jsapi_x5.wx.mode;

import com.baidu.location.BDLocation;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020/9/24.
 *
 * @author godliness
 */
public final class LocationResult {

    /**
     * 纬度，范围为 -90~90，负数表示南纬
     */
    @Expose
    @SerializedName("latitude")
    public double latitude;

    /**
     * 经度，范围为 -180~180，负数表示西经
     */
    @Expose
    @SerializedName("longitude")
    public double longitude;

    /**
     * 速度，单位 m/s
     */
    @Expose
    @SerializedName("speed")
    public float speed;

    /**
     * 位置的精确度
     */
    @Expose
    @SerializedName("accuracy")
    public int accuracy;

    /**
     * 高度，单位 m
     */
    @Expose
    @SerializedName("altitude")
    public double altitude;

    @Expose
    @SerializedName("address")
    public String address;

    public static LocationResult toResult(BDLocation location) {
        final LocationResult result = new LocationResult();
        result.latitude = location.getLatitude();
        result.longitude = location.getLongitude();
        result.speed = location.getSpeed();
        result.accuracy = location.getGpsAccuracyStatus();
        result.altitude = location.getAltitude();
        result.address = location.getAddrStr();
        return result;
    }

    @Override
    public String toString() {
        return "LocationResult{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", speed=" + speed +
                ", accuracy=" + accuracy +
                ", altitude=" + altitude +
                ", address='" + address + '\'' +
                '}';
    }
}
