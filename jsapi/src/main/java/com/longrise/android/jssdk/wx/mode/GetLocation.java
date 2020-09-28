package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.longrise.android.location.mode.LocationParams;

/**
 * Created by godliness on 2020/9/23.
 *
 * @author godliness
 */
public final class GetLocation {

    @Expose
    @SerializedName("type")
    public String type;

    @Expose
    @SerializedName("altitude")
    public boolean altitude;


    @Expose
    @SerializedName("isHighAccuracy")
    public boolean isHighAccuracy;

    @Expose
    @SerializedName("highAccuracyExpireTime")
    public int highAccuracyExpireTime;

    @Expose
    @SerializedName("isAddress")
    public boolean isAddress;

    public LocationParams toLocationParams() {
        final LocationParams params = new LocationParams();
        params.type = type;
        params.isAddress = isAddress;
        params.highAccuracyExpireTime = highAccuracyExpireTime;
        params.isAltitude = altitude;
        params.isHighAccuracy = isHighAccuracy;
        return params;
    }
}
