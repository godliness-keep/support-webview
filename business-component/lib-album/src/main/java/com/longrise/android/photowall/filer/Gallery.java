package com.longrise.android.photowall.filer;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.photowall.Filer;
import com.longrise.android.result.ActivityOnResult;
import com.longrise.android.result.OnActivityResultListener;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 * 相册选择
 */
public final class Gallery {

    private Filer.IGalleryListener mGalleryCallback;

    public void start(Activity host) {
        start(host, new OnActivityResultListener() {

            @Override
            public void onActivityResult(int resultCode, @NonNull Intent data) {
                if (mGalleryCallback != null) {
                    mGalleryCallback.onSelected(resultCode == Activity.RESULT_OK ? data.getData() : null);
                }
            }
        });
    }

    public void start(Activity host, OnActivityResultListener onResultListener) {
        ActivityOnResult.from((FragmentActivity) host).onResult(onResultListener).to(getIntent());
    }

    public Gallery() {

    }

    public Gallery(Filer.IGalleryListener callback) {
        this.mGalleryCallback = callback;
    }

    private Intent getIntent() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }
}
