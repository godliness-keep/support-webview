package com.longrise.android.photowall;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.longrise.android.result.IActivityOnResultListener;

import java.io.File;

/**
 * Created by godliness on 2020/9/9.
 *
 * @author godliness
 */
public final class Album {

    public static final String RESULT = "result";

    public interface SizeType {
        int ALL = 3;
        /**
         * 原图
         */
        int ORIGINAL = 2;
        /**
         * 缩略图
         */
        int COMPRESSED = 1;
    }

    public interface SourceType {
        int ALL = 3;
        /**
         * 相册
         */
        int ALBUM = 2;
        /**
         * 相机
         */
        int CAMERA = 1;
    }

    public interface IChooserCallback {

        /**
         * 已选中的相片
         *
         * @param values 相片地址
         */
        void onSelected(String[] values);
    }

    public interface ITakeListener {

        /**
         * 拍照返回
         *
         * @param uri 图片地址
         */
        void onTaken(@Nullable Uri uri);
    }

    public interface ICropListener {

        /**
         * 裁剪返回
         *
         * @param uri 图片地址
         */
        void onCrop(@Nullable Uri uri);
    }

    public interface IGalleryListener {

        /**
         * 系统相册返回
         *
         * @param uri 图片地址
         */
        void onSelected(@Nullable Uri uri);
    }

    /**
     * 预览图片  todo 样式待完善
     */
    public static Preview previewOf(String current, @Nullable String[] values) {
        return new Preview(current, values);
    }

    /**
     * 选择图片，通过 Callback 返回选择结果
     */
    public static Chooser.ChooserCallback chooseOf(@NonNull Album.IChooserCallback chooserCallback) {
        return Chooser.choose(chooserCallback);
    }

    /**
     * 选择图片，通过 onActivityResult 返回选择结果
     */
    public static Chooser.ChooserResult chooseOf() {
        return Chooser.choose();
    }

    /**
     * 调用系统拍照，并指定项目输出目录
     */
    public static Take takeOf(@NonNull File out) {
        return new Take(out);
    }

    /**
     * 调用系统拍照，通过 {@link ITakeListener} 回调返回
     */
    public static Take takeOf(@NonNull ITakeListener takeListener) {
        return new Take(takeListener);
    }

    /**
     * 调用系统相册
     */
    public static Gallery galleryOf() {
        return new Gallery();
    }

    /**
     * 调用系统相册
     */
    public static Gallery galleryOf(IGalleryListener callback) {
        return new Gallery(callback);
    }

    /**
     * 调用图片剪裁，并指定项目输出目录
     */
    public static CropOf cropOf(@NonNull Uri src, @NonNull Uri out) {
        return new CropOf(src, out);
    }

    /**
     * 调用图片剪裁
     */
    public static CropOf cropOf(@NonNull Uri src, ICropListener cropListener) {
        return new CropOf(src, cropListener);
    }

    private Album() {
    }
}
