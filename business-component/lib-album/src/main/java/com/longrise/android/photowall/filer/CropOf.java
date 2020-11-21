package com.longrise.android.photowall.filer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.image.crop.Crop;
import com.longrise.android.image.crop.CropImageActivity;
import com.longrise.android.photowall.Filer;
import com.longrise.android.photowall.R;
import com.longrise.android.photowall.utils.Utils;
import com.longrise.android.result.ActivityResult;
import com.longrise.android.result.IActivityOnResultListener;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 * 图片裁剪
 */
public final class CropOf {

    private Filer.ICropListener mCropCallback;

    private final Uri mSrc;
    private Uri mOut;

    /**
     * 2英寸：413 * 626
     * 基于宽度 413 计算：413 / 3
     * 基于高度 626 计算：626 / 4
     * 基于宽度的匹配：411：548
     * 基于高度的匹配：468：624
     */
    private int mWidth = 468;
    private int mHeight = 624;

    private int mX = 3;
    private int mY = 4;

    private int mTips = R.string.album_string_choose_box;
    private boolean mPng;

    public CropOf(@NonNull Uri src, @NonNull Uri out) {
        this.mSrc = src;
        this.mOut = out;
    }

    public CropOf(@NonNull Uri src, @NonNull Filer.ICropListener cropListener) {
        this.mSrc = src;
        this.mCropCallback = cropListener;
    }

    public CropOf cropCallback(Filer.ICropListener cropCallback) {
        this.mCropCallback = cropCallback;
        return this;
    }

    public CropOf withMaxSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
        return this;
    }

    public CropOf withAspect(int x, int y) {
        this.mX = x;
        this.mY = y;
        return this;
    }

    public CropOf asPng(boolean savePng) {
        this.mPng = savePng;
        return this;
    }

    public CropOf withTips(@StringRes int tips) {
        this.mTips = tips;
        return this;
    }

    public void start(Activity host) {
        if (mOut == null) {
            mOut = Utils.getLocalUri(host, "temp_crop.jpg");
        }

        final Intent intent = getIntent();
        intent.setClass(host, CropImageActivity.class);
        ActivityResult.from((FragmentActivity) host)
                .onResult(new IActivityOnResultListener() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        if (mCropCallback != null) {
                            mCropCallback.onCrop(resultCode == Activity.RESULT_OK ? Crop.getOutput(data) : null);
                        }
                    }
                }).to(intent);
    }

    private Intent getIntent() {
        final Intent intent = new Intent();
        intent.setData(mSrc);
        intent.putExtra("aspect_x", mX);
        intent.putExtra("aspect_y", mY);
        intent.putExtra("max_x", mWidth);
        intent.putExtra("max_y", mHeight);
        intent.putExtra("output", mOut);
        intent.putExtra("as_png", mPng);
        intent.putExtra("tips", mTips);
        return intent;
    }
}
