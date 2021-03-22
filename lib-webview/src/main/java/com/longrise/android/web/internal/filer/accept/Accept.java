package com.longrise.android.web.internal.filer.accept;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.permission.IResult;
import com.longrise.android.permission.OnPermissionResultListener;
import com.longrise.android.permission.RequestPermission;
import com.longrise.android.result.ActivityOnResult;
import com.longrise.android.result.OnActivityResultListener;
import com.longrise.android.web.R;
import com.longrise.android.web.internal.filer.IFilerListener;

import java.io.File;

/**
 * Created by godliness on 2020/11/20.
 *
 * @author godliness
 */
public abstract class Accept {

    private final FragmentActivity mCxt;
    private final String mType;

    public abstract void onClick(@NonNull IFilerListener onResultListener);

    public abstract String getName();

    final FragmentActivity getHost() {
        return mCxt;
    }

    final String getType() {
        return mType;
    }

    final String getString(@StringRes int stringId) {
        return mCxt.getString(stringId);
    }

    Accept(FragmentActivity cxt, String type) {
        this.mCxt = cxt;
        this.mType = type;
    }

    static final class AlbumAccept extends Accept {

        AlbumAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(@NonNull final IFilerListener filerListener) {
            ActivityOnResult.from(getHost()).onResult(filerListener).to(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        }

        @Override
        public String getName() {
            return getString(R.string.filer_image);
        }
    }

    static final class CameraAccept extends Accept {

        CameraAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(@NonNull final IFilerListener callback) {
            final FragmentActivity host = getHost();
            RequestPermission.of(host).onResult(new OnPermissionResultListener() {
                @Override
                public void onResult(@NonNull IResult iResult) {
                    if (iResult.isGranted()) {
                        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        final File outFile = Utils.getLocalFile(host, "temp_camera.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.transformProviderUri(host, outFile));
                        ActivityOnResult.from(host).onResult(new OnActivityResultListener() {
                            @Override
                            public void onActivityResult(int resultCode, Intent data) {
                                final Intent intent = new Intent();
                                intent.setData(Uri.fromFile(outFile));
                                callback.onActivityResult(resultCode, intent);
                            }
                        }).to(intent);
                    } else {
                        callback.onReceiveValueEnd();

                        if (iResult.isClosed()) {
                            iResult.showSettingDialog();
                        }
                    }
                }
            }).check(Manifest.permission.CAMERA);
        }

        @Override
        public String getName() {
            return getString(R.string.filer_camera);
        }
    }

    static final class VideoAccept extends Accept {

        VideoAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(@NonNull IFilerListener filerListener) {
            final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(getType());
            ActivityOnResult.from(getHost()).onResult(filerListener).to(intent);
        }

        @Override
        public String getName() {
            return getString(R.string.filer_video);
        }
    }

    static final class VideoRecorderAccept extends Accept {

        VideoRecorderAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(@NonNull final IFilerListener callback) {
            final FragmentActivity host = VideoRecorderAccept.this.getHost();
            RequestPermission.of(host).onResult(new OnPermissionResultListener() {
                @Override
                public void onResult(@NonNull IResult iResult) {
                    if (iResult.isGranted()) {
                        final File outFile = new File(host.getExternalFilesDir("video"), System.currentTimeMillis() + ".mp4");
                        final Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.transformProviderUri(host, outFile));
                        ActivityOnResult.from(host).onResult(callback).to(intent);
                    } else {
                        callback.onReceiveValueEnd();

                        if (iResult.isClosed()) {
                            iResult.showSettingDialog();
                        }
                    }
                }
            }).check(Manifest.permission.CAMERA);
        }

        @Override
        public String getName() {
            return getString(R.string.filer_video_recorder);
        }
    }

    static final class Audio extends Accept {

        Audio(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(@NonNull final IFilerListener callback) {
            final FragmentActivity host = getHost();
            RequestPermission.of(host).onResult(new OnPermissionResultListener() {
                @Override
                public void onResult(@NonNull IResult iResult) {
                    if (iResult.isGranted()) {
                        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType(getType());
                        ActivityOnResult.from(host).onResult(callback).to(intent);
                    } else {
                        callback.onReceiveValueEnd();

                        if (iResult.isClosed()) {
                            iResult.showSettingDialog();
                        }
                    }
                }
            }).check(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        @Override
        public String getName() {
            return getString(R.string.filer_audio);
        }
    }

    static final class AudioRecorderAccept extends Accept {

        AudioRecorderAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(@NonNull final IFilerListener filerListener) {
            final Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            ActivityOnResult.from(getHost()).onResult(filerListener).to(intent);
        }

        @Override
        public String getName() {
            return getString(R.string.filer_audio_recorder);
        }
    }

    static final class FilerAccept extends Accept {

        FilerAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(@NonNull final IFilerListener callback) {
            final FragmentActivity host = getHost();
            RequestPermission.of(host).onResult(new OnPermissionResultListener() {
                @Override
                public void onResult(@NonNull IResult iResult) {
                    if (iResult.isGranted()) {
                        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType(getType());
                        ActivityOnResult.from(host).onResult(callback).to(intent);
                    } else {
                        callback.onReceiveValueEnd();

                        if (iResult.isClosed()) {
                            iResult.showSettingDialog();
                        }
                    }
                }
            }).check(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        @Override
        public String getName() {
            return getString(R.string.filer);
        }
    }
}
