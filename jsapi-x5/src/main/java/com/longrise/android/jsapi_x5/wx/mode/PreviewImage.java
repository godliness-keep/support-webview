package com.longrise.android.jsapi_x5.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020-04-19.
 *
 * @author godliness
 */
public final class PreviewImage {

    @Expose
    @SerializedName("current")
    private String current;

    @Expose
    @SerializedName("urls")
    private String[] urls;

    public String getCurrent() {
        return current;
    }

    public String[] getUrls() {
        return urls;
    }
}
