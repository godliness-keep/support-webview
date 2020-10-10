package com.longrise.android.jssdk.wx.mode;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by godliness on 2020/9/15.
 *
 * @author godliness
 */
public final class CropImage {

    /**
     * 是否需要剪裁
     */
    @Expose
    @SerializedName("crop")
    public boolean crop = true;

    /**
     * 原图片地址
     */
    @Expose
    @SerializedName("src")
    private String src;

    /**
     * 剪裁宽度
     */
    @Expose
    @SerializedName("width")
    private int width = 468;

    /**
     * 剪裁高度
     */
    @Expose
    @SerializedName("height")
    private int height = 624;

    /**
     * X 轴比例，默认 3:4
     */
    @Expose
    @SerializedName("x")
    private int x = 3;

    /**
     * Y 轴比例，默认 3:4
     */
    @Expose
    @SerializedName("y")
    private int y = 4;

    @Nullable
    public Uri getSource() {
        try {
            return Uri.parse(src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "CropImage{" +
                "crop=" + crop +
                ", src='" + src + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
