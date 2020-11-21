package com.longrise.android.photowall;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;

import com.longrise.android.result.ActivityResult;
import com.longrise.android.result.IActivityOnResultListener;

import java.io.File;

/**
 * Created by godliness on 2020/11/21.
 *
 * @author godliness
 */
public final class VideoRecorder {

    private final Intent mIntent;
    private File mOut;
    private IActivityOnResultListener mResultListener;

    public VideoRecorder() {
        this(null);
    }

    public VideoRecorder(File out) {
        this.mOut = out;
        this.mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }

    public VideoRecorder onResult(IActivityOnResultListener onResultListener) {
        this.mResultListener = onResultListener;
        return this;
    }

    public void start(FragmentActivity host) {
        if (mOut == null) {
            mOut = new File(host.getExternalFilesDir("video"), System.currentTimeMillis() + ".mp4");
        }
//        final Uri fileUri = FileProvider.getUriForFile(host.getApplicationContext(), host.getPackageName() + ".fileprovider", mOut);
        final Uri fileUri = Utils.transformProviderUri(host, mOut);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        ActivityResult.from(host).onResult(mResultListener).to(mIntent);
    }
}
