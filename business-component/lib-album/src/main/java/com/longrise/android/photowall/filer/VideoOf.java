package com.longrise.android.photowall.filer;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.result.ActivityOnResult;
import com.longrise.android.result.OnActivityResultListener;

/**
 * Created by godliness on 2020/11/21.
 *
 * @author godliness
 */
public final class VideoOf {

    private final Intent mIntent;
    private OnActivityResultListener mResultListener;

    public VideoOf(OnActivityResultListener onResultListener) {
        this("video/*");
        this.mResultListener = onResultListener;
    }

    public VideoOf(String mimeType) {
        this.mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        this.mIntent.addCategory(Intent.CATEGORY_OPENABLE);
        this.mIntent.setType(mimeType);
    }

    public VideoOf onResult(OnActivityResultListener onResultListener) {
        this.mResultListener = onResultListener;
        return this;
    }

    public void start(FragmentActivity host) {
        ActivityOnResult.from(host).onResult(mResultListener).to(mIntent);
    }
}
