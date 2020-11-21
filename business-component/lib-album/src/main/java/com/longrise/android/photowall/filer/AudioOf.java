package com.longrise.android.photowall.filer;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.result.ActivityResult;
import com.longrise.android.result.IActivityOnResultListener;

/**
 * Created by godliness on 2020/11/21.
 *
 * @author godliness
 */
public abstract class AudioOf {

    IActivityOnResultListener mResultListener;
    final Intent mIntent;

    public AudioOf(IActivityOnResultListener onResultListener) {
        this.mResultListener = onResultListener;
        this.mIntent = new Intent();
    }

    public final void start(FragmentActivity host) {
        ActivityResult.from(host).onResult(mResultListener).to(mIntent);
    }

    public static class AudioFiler extends AudioOf {

        public AudioFiler(IActivityOnResultListener resultListener) {
            super(resultListener);
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            mIntent.addCategory(Intent.CATEGORY_OPENABLE);
            mIntent.setType("audio/*");
        }

        public AudioFiler(@NonNull String mimeType) {
            this((IActivityOnResultListener) null);
            mIntent.setType(mimeType);
        }

        public AudioFiler onResult(IActivityOnResultListener resultListener) {
            this.mResultListener = resultListener;
            return this;
        }
    }

    public static class AudioRecorder extends AudioOf {

        public AudioRecorder(IActivityOnResultListener onResultListener) {
            super(onResultListener);
            mIntent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        }
    }
}
