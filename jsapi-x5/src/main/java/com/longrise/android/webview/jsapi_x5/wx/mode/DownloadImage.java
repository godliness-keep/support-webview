package com.longrise.android.jssdk.wx.mode;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public final class DownloadImage {

    @Expose
    @SerializedName("serverId")
    private String serverId;

    @Expose
    @SerializedName("isShowProgressTips")
    private int isShowProgressTips;

    public String getServerId() {
        return serverId;
    }

    public int getIsShowProgressTips() {
        return isShowProgressTips;
    }

    public String getResName(){
        if(!TextUtils.isEmpty(serverId)){
            final int index = serverId.lastIndexOf("/");
            return serverId.substring(index + 1);
        }
        return null;
    }
}
