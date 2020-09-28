package com.longrise.android.photowall.album;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by godliness on 16/3/31.
 *
 * @author godliness
 */
final class LoadMediaTask extends Thread {

    private static final String TAG = "LoadMediaTask";

    private final ContentResolver mResolver;
    private final WeakReference<OnMediaLoadListener> mCallback;

    public LoadMediaTask(Context cxt, OnMediaLoadListener loadListener) {
        this.mResolver = cxt.getContentResolver();
        this.mCallback = new WeakReference<>(loadListener);
    }

    public interface OnMediaLoadListener {

        /**
         * Media 加载结束
         *
         * @param mediaData {@link MediaData}
         */
        void onMediaLoaded(@Nullable MediaData mediaData);
    }

    @Override
    public void run() {
        final Cursor cursor = mResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ? or " + MediaStore.Images.Media.MIME_TYPE + " = ?",
                new String[]{"image/jpeg", "image/png", "image/jpg"}, MediaStore.Images.Media.DATE_ADDED + " desc");


//        final Cursor cursor = mResolver.query(
//                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
//                new String[]{
//                        MediaStore.Images.Thumbnails.IMAGE_ID,
//                        MediaStore.Images.Thumbnails.DATA},
//                null,
//                null,
//                null
//        );

        try {
            callback(parseMediaInCursor(cursor));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
        }
    }

    private MediaData parseMediaInCursor(Cursor cursor) {
        final int size;
        if (cursor == null || (size = cursor.getCount()) <= 0) {
            return null;
        }

        final ArrayMap<String, Folder> contains = new ArrayMap<>();
        final MediaData mediaData = new MediaData(size);
        while (cursor.moveToNext()) {
            final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            final File parentFile = new File(path).getParentFile();
            if (parentFile == null) {
                continue;
            }
            mediaData.addFile(path);

            final String dirPath = parentFile.getAbsolutePath();
            Folder folder = contains.get(dirPath);
            if (folder != null) {
                folder.addFile(path);
            } else {
                contains.put(dirPath, folder = new Folder());
                folder.setFolderDir(dirPath);
                folder.setFirstFilePath(path);
                folder.addFile(path);
                mediaData.addFolder(folder);
            }
        }
        return mediaData;
    }

//    private MediaData parseMediaInCursor(Cursor cursor) {
//        final int size;
//        if (cursor == null || (size = cursor.getCount()) <= 0) {
//            return null;
//        }
//
//        final ArraySet<String> contains = new ArraySet<>();
//        final MediaData mediaData = new MediaData(size);
//        while (cursor.moveToNext()) {
//            final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//            final File parentFile = new File(path).getParentFile();
//            if (parentFile == null) {
//                continue;
//            }
//            mediaData.addFile(path);
//
//            final String dirPath = parentFile.getAbsolutePath();
//            if (contains.contains(dirPath)) {
//                continue;
//            }
//
//            contains.add(dirPath);
//            final Folder folder = new Folder();
//            folder.setFolderDir(dirPath);
//            folder.setFirstFilePath(path);
//            mediaData.addFolder(folder);
//        }
//        return mediaData;
//    }

    private void callback(MediaData mediaData) {
        final OnMediaLoadListener callback = mCallback.get();
        if (callback != null) {
            callback.onMediaLoaded(mediaData);
        }
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
