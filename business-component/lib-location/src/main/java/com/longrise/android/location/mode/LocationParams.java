package com.longrise.android.location.mode;

/**
 * Created by godliness on 2020/9/23.
 *
 * @author godliness
 */
public final class LocationParams {

    /**
     * 设置返回经纬度坐标类型
     * gcj02：国测局坐标，默认
     * bd09ll：百度经纬度坐标
     * bd09：百度墨卡托坐标
     * wgs84：全球 gps 坐标
     */
    public String type = "gcj02";

    /**
     * 是否采用高精度定位
     */
    public boolean isHighAccuracy;

    /**
     * 是否需要高度信息
     */
    public boolean isAltitude;

    /**
     * 高精度定位超时时间(ms)，指定时间内返回最高精度，该值1000ms以上高精度定位才有效果
     */
    public int highAccuracyExpireTime;

    /**
     * 是否需要地址信息
     */
    public boolean isAddress;

    public static LocationParams getDefault() {
        return new LocationParams();
    }

    public boolean isOnce() {
        return highAccuracyExpireTime <= 1000;
    }
}



