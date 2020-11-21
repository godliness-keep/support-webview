package com.longrise.android.photowall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.longrise.android.photowall.album.PhotosActivity;

/**
 * Created by godliness on 2020/9/14.
 *
 * @author godliness
 * 照片墙，多选
 */
public abstract class Chooser<T> {

    private AlbumParams mParams;

    public static ChooserCallback choose(Filer.IChooserCallback chooserCallback) {
        return new ChooserCallback(chooserCallback);
    }

    public static ChooserResult choose() {
        return new ChooserResult();
    }

    public static void notifyChooseResult(Activity target, String[] values) {
        ChooserCallback.callback(values);
        ChooserResult.setResultToFinish(target, values);
    }

    @SuppressWarnings("unchecked")
    public T params(int count, int sizeType, int sourceType) {
        mParams = new AlbumParams();
        mParams.count = count;
        mParams.sizeType = sizeType;
        mParams.sourceType = sourceType;
        return (T) this;
    }

    public static final class ChooserCallback extends Chooser<ChooserCallback> {

        static volatile ChooserCallback sChooser;
        private final Filer.IChooserCallback mCallback;

        ChooserCallback(Filer.IChooserCallback chooserCallback) {
            this.mCallback = chooserCallback;
        }

        public void to(@NonNull Context host) {
            host.startActivity(createIntent(host));
            sChooser = this;
        }

        static void callback(String[] values) {
            if (sChooser != null) {
                sChooser.onSelected(values);
                sChooser = null;
            }
        }

        void onSelected(String[] values) {
            if (mCallback != null) {
                mCallback.onSelected(values);
            }
        }
    }

    public static final class ChooserResult extends Chooser<ChooserResult> {

        ChooserResult() {

        }

        public void to(@NonNull Activity host, int requestCode) {
            host.startActivityForResult(createIntent(host), requestCode);
        }

        static void setResultToFinish(Activity target, String[] values) {
            final Intent intent = new Intent();
            intent.putExtra(Filer.RESULT, values);
            target.setResult(Activity.RESULT_OK, intent);
            target.finish();
        }
    }

    Intent createIntent(Context cxt) {
        final Intent intent = new Intent(cxt, PhotosActivity.class);
        if (mParams == null) {
            mParams = new AlbumParams();
        }
        intent.putExtra(AlbumParams.EXTRA_PARAMS, mParams);
        return intent;
    }

    Chooser() {

    }
}
