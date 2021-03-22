package com.longrise.android.photowall.filer;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.photowall.utils.Utils;
import com.longrise.android.result.ActivityOnResult;
import com.longrise.android.result.OnActivityResultListener;

import java.io.File;

/**
 * Created by godliness on 2020/11/21.
 *
 * @author godliness
 */
public final class VideoRecorder {

    private final Intent mIntent;
    private File mOut;
    private OnActivityResultListener mResultListener;

    public VideoRecorder() {
        this(null);
    }

    public VideoRecorder(File out) {
        this.mOut = out;
        this.mIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    }

    public VideoRecorder onResult(OnActivityResultListener onResultListener) {
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
        ActivityOnResult.from(host).onResult(mResultListener).to(mIntent);
    }
}
