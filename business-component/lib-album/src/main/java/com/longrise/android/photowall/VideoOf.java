package com.longrise.android.photowall;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.result.ActivityResult;
import com.longrise.android.result.IActivityOnResultListener;

/**
 * Created by godliness on 2020/11/21.
 *
 * @author godliness
 */
public final class VideoOf {

    private final Intent mIntent;
    private IActivityOnResultListener mResultListener;

    public VideoOf(IActivityOnResultListener onResultListener) {
        this("video/*");
        this.mResultListener = onResultListener;
    }

    public VideoOf(String mimeType) {
        this.mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        this.mIntent.addCategory(Intent.CATEGORY_OPENABLE);
        this.mIntent.setType(mimeType);
    }

    public VideoOf onResult(IActivityOnResultListener onResultListener) {
        this.mResultListener = onResultListener;
        return this;
    }

    public void start(FragmentActivity host) {
        ActivityResult.from(host).onResult(mResultListener).to(mIntent);
    }
}
