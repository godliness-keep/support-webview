package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020/9/24.
 *
 * @author godliness
 */
public final class ShareTo {

    @Expose
    @SerializedName("title")
    public String title;

    @Expose
    @SerializedName("desc")
    public String desc;

    @Expose
    @SerializedName("link")
    public String link;

    @Expose
    @SerializedName("imgUrl")
    public String imgUrl;

    @Expose
    @SerializedName("type")
    public String type;

    @Expose
    @SerializedName("dataUrl")
    public String dataUrl;
}
