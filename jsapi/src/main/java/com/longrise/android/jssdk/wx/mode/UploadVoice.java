package com.longrise.android.jssdk.wx.mode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public final class UploadVoice {

    @Expose
    @SerializedName("localId")
    private String localId;

    @Expose
    @SerializedName("isShowProgressTips")
    private int isShowProgressTips;

    public String getLocalId() {
        return localId;
    }

    public int getIsShowProgressTips() {
        return isShowProgressTips;
    }
}
