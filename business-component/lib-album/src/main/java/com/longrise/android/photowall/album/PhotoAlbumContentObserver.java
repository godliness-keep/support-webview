package com.longrise.android.photowall.album;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

/**
 * Created by godliness on 2020/9/10.
 *
 * @author godliness
 */
final class PhotoAlbumContentObserver extends ContentObserver {

    private static final String TAG = "PhotoAlbumContentObserver";

    private Context mCxt;
    private OnChangeListener mChangeListener;

    interface OnChangeListener {
        /**
         * 相册发生变化
         *
         * @param uri Uri
         */
        void onChanged(Uri uri);
    }

    public PhotoAlbumContentObserver(Context cxt) {
        super(new Handler(Looper.getMainLooper()));
        this.mCxt = cxt;
        if (cxt instanceof OnChangeListener) {
            this.mChangeListener = (OnChangeListener) cxt;
        }
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        if (mChangeListener != null) {
            mChangeListener.onChanged(uri);
        }
    }

    static PhotoAlbumContentObserver registerObserver(Context context) {
        final Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        final PhotoAlbumContentObserver observer = new PhotoAlbumContentObserver(context);
        try {
            context.getContentResolver().registerContentObserver(imageUri, false, observer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return observer;
    }

    void unregisterObserver() {
        if (mCxt != null) {
            try {
                mCxt.getContentResolver().unregisterContentObserver(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
