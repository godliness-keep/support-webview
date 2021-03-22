package com.longrise.android.photowall.filer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.photowall.Filer;
import com.longrise.android.photowall.utils.Utils;
import com.longrise.android.result.ActivityOnResult;
import com.longrise.android.result.OnActivityResultListener;

import java.io.File;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 * 拍照
 */
public final class Take {

    private Filer.ITakeListener mTakeCallback;

    private File mOut;

    public Take(File out) {
        this.mOut = out;
    }

    public Take(@NonNull Filer.ITakeListener takeListener) {
        this.mTakeCallback = takeListener;
    }

    public Take callback(Filer.ITakeListener callback) {
        this.mTakeCallback = callback;
        return this;
    }

    public void start(Activity host) {
        if (mOut == null) {
            mOut = Utils.getLocalFile(host, "temp_camera.jpg");
        }
        final Uri outUri = Utils.transformProviderUri(host, mOut);
        ActivityOnResult.from((FragmentActivity) host).onResult(new OnActivityResultListener() {
            @Override
            public void onActivityResult(int resultCode, @NonNull Intent data) {
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
