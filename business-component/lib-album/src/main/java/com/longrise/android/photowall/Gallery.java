package com.longrise.android.photowall;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.result.ActivityResult;
import com.longrise.android.result.IActivityOnResultListener;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 * 相册选择
 */
public final class Gallery {

    private final Album.IGalleryListener mGalleryCallback;

    Gallery(Album.IGalleryListener callback) {
        this.mGalleryCallback = callback;
    }

    public void start(Activity host) {
        ActivityResult.from((FragmentActivity) host)
                .onResult(new IActivityOnResultListener() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        if (mGalleryCallback != null) {
                            mGalleryCallback.onSelected(resultCode == Activity.RESULT_OK ? data.getData() : null);
                        }
                    }
                }).to(getIntent());
    }

    private Intent getIntent() {
        return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }
}
