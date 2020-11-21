package com.longrise.android.web.internal.filer.accept;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.permission.IPermissionHelper;
import com.longrise.android.permission.RequestPermission;
import com.longrise.android.photowall.Filer;
import com.longrise.android.web.R;
import com.longrise.android.web.internal.filer.IFilerListener;

/**
 * Created by godliness on 2020/11/20.
 *
 * @author godliness
 */
public abstract class Accept {

    private final FragmentActivity mCxt;
    private final String mType;

    public abstract void onClick(IFilerListener onResultListener);

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
        public void onClick(IFilerListener filerListener) {
            Filer.galleryOf().start(getHost(), filerListener);
        }

        @Override
        public String getName() {
            return getHost().getString(R.string.filer_image);
        }
    }

    static final class CameraAccept extends Accept {

        CameraAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(final IFilerListener filerListener) {
            RequestPermission.of(getHost())
                    .onPermissionResult(new IPermissionHelper() {
                        @Override
                        protected boolean onPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
                            if (isGranted()) {
                                Filer.takeOf(new Filer.ITakeListener() {
                                    @Override
                                    public void onTaken(@Nullable Uri uri) {
                                        final Intent intent = new Intent();
                                        intent.setData(uri);
                                        filerListener.onActivityResult(FragmentActivity.RESULT_OK, intent);
                                    }
                                }).start(CameraAccept.this.getHost());
                            } else {
                                filerListener.onReceiveValueEnd();
                            }
                            return false;
                        }
                    }).request(Manifest.permission.CAMERA);
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
        public void onClick(IFilerListener filerListener) {
            Filer.videoFiler(getType()).onResult(filerListener).start(getHost());
        }

        @Override
        public String getName() {
            return getString(R.string.filer_video);
        }
    }

    static class VideoRecorderAccept extends Accept {

        VideoRecorderAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(final IFilerListener filerListener) {
            RequestPermission.of(getHost()).onPermissionResult(new IPermissionHelper() {
                @Override
                protected boolean onPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
                    final FragmentActivity host = VideoRecorderAccept.this.getHost();
                    if (isGranted()) {
                        Filer.videoRecorder(filerListener).start(host);

                    } else {
                        filerListener.onReceiveValueEnd();
                    }
                    return false;
                }
            }).request(Manifest.permission.CAMERA);
        }

        @Override
        public String getName() {
            return getString(R.string.filer_video_recorder);
        }
    }

    static class Audio extends Accept {

        Audio(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(final IFilerListener filerListener) {
            RequestPermission.of(getHost())
                    .onPermissionResult(new IPermissionHelper() {
                        @Override
                        protected boolean onPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
                            if (isGranted()) {
                                Filer.audioFiler(getType()).onResult(filerListener).start(Audio.this.getHost());
                            } else {
                                filerListener.onReceiveValueEnd();
                            }
                            return false;
                        }
                    }).request(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        @Override
        public String getName() {
            return getString(R.string.filer_audio);
        }
    }

    static class AudioRecorderAccept extends Accept {

        AudioRecorderAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(final IFilerListener filerListener) {
            Filer.audioRecorder(filerListener).start(getHost());
        }

        @Override
        public String getName() {
            return getString(R.string.filer_audio_recorder);
        }
    }

    static class FilerAccept extends Accept {

        FilerAccept(FragmentActivity cxt, String type) {
            super(cxt, type);
        }

        @Override
        public void onClick(final IFilerListener filerListener) {
            RequestPermission.of(getHost())
                    .onPermissionResult(new IPermissionHelper() {
                        @Override
                        protected boolean onPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
                            if (isGranted()) {
                                Filer.filer(getType()).onResult(filerListener).start(FilerAccept.this.getHost());
                            } else {
                                filerListener.onReceiveValueEnd();
                            }
                            return false;
                        }
                    }).request(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        @Override
        public String getName() {
            return getString(R.string.filer);
        }
    }
}
