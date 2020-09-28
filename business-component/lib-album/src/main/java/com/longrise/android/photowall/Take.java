package com.longrise.android.photowall;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.result.ActivityResult;
import com.longrise.android.result.IActivityOnResultListener;

import java.io.File;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 * 拍照
 */
public final class Take {

    private Album.ITakeListener mTakeCallback;

    private File mOut;

    Take(File out) {
        this.mOut = out;
    }

    Take(@NonNull Album.ITakeListener takeListener) {
        this.mTakeCallback = takeListener;
    }

    public Take callback(Album.ITakeListener callback) {
        this.mTakeCallback = callback;
        return this;
    }

    public void start(Activity host) {
        if (mOut == null) {
            mOut = Utils.getLocalFile(host, "temp_camera.jpg");
        }
        final Uri outUri = Utils.transformProviderUri(host, mOut);
        ActivityResult.from((FragmentActivity) host)
                .onResult(new IActivityOnResultListener() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        if (mTakeCallback != null) {
                            mTakeCallback.onTaken(resultCode == Activity.RESULT_OK ? Uri.fromFile(mOut) : null);
                        }
                    }
                }).to(getIntent(outUri));
    }

    private Intent getIntent(Uri outUri) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
        return intent;
    }
}
