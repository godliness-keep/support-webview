package com.longrise.android.photowall.filer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.result.ActivityOnResult;
import com.longrise.android.result.OnActivityResultListener;

/**
 * Created by godliness on 2020/11/21.
 *
 * @author godliness
 */
public final class FilerOf {

    private Intent mIntent;
    private OnActivityResultListener mResultListener;

    public FilerOf(@NonNull OnActivityResultListener resultListener) {
        this.mResultListener = resultListener;
        create("*/*");
    }

    public FilerOf(@NonNull String mimeType) {
        create(mimeType);
    }

    public FilerOf onResult(@NonNull OnActivityResultListener resultListener) {
        this.mResultListener = resultListener;
        return this;
    }

    public void start(FragmentActivity host) {
        ActivityOnResult.from(host).onResult(mResultListener).to(mIntent);
    }

    private void create(String mimeType) {
        this.mIntent = new Intent(Intent.ACTION_GET_CONTENT);
        this.mIntent.addCategory(Intent.CATEGORY_OPENABLE);
        this.mIntent.setType(mimeType);
    }
}
