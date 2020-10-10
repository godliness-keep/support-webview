package com.longrise.android.jssdk.wx.mode;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.longrise.android.photowall.Album;

import java.util.Arrays;

/**
 * Created by godliness on 2020-04-17.
 *
 * @author godliness
 */
public final class ChooseImage {

    @Expose
    @SerializedName("count")
    private int count;

    @Expose
    @SerializedName("sizeType")
    private String[] sizeType;

    @Expose
    @SerializedName("sourceType")
    private String[] sourceType;

    public int getCount() {
        if (count > 9) {
            return 9;
        } else if (count <= 0) {
            return 1;
        }
        return count;
    }

    public int getSizeType() {
        if (sizeType == null) {
            return Album.SizeType.ALL;
        }
        if (sizeType.length > 2) {
            throw new IllegalArgumentException("sizeType params is illegal");
        }

        boolean original = false;
        boolean compressed = false;

        for (String s : sizeType) {
            if (TextUtils.equals(s, "original")) {
                original = true;
            } else if (TextUtils.equals(s, "compressed")) {
                compressed = true;
            }
        }

        if (original && compressed) {
            return Album.SizeType.ALL;
        } else if (original) {
            return Album.SizeType.ORIGINAL;
        } else if (compressed) {
            return Album.SizeType.COMPRESSED;
        } else {
            throw new IllegalArgumentException("sizeType params is illegal");
        }
    }

    public int getSourceType() {
        if (sourceType == null) {
            return Album.SourceType.ALL;
        }
        if (sourceType.length > 2) {
            throw new IllegalArgumentException("sourceType params is illegal");
        }

        boolean album = false;
        boolean camera = false;
        for (String s : sourceType) {
            if (TextUtils.equals(s, "album")) {
                album = true;
            } else if (TextUtils.equals(s, "camera")) {
                camera = true;
            }
        }

        if (album && camera) {
            return Album.SourceType.ALL;
        } else if (album) {
            return Album.SourceType.ALBUM;
        } else if (camera) {
            return Album.SourceType.CAMERA;
        } else {
            throw new IllegalArgumentException("sourceType params is illegal");
        }
    }

    @Override
    public String toString() {
        return "{" +
                "count=" + count +
                ", sizeType=" + Arrays.toString(sizeType) +
                ", sourceType=" + Arrays.toString(sourceType) +
                '}';
    }
}
