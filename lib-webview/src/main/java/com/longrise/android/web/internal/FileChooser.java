package com.longrise.android.web.internal;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import com.longrise.android.jssdk.wx.BridgeApi;
import com.longrise.android.permission.IPermissionHelper;
import com.longrise.android.permission.RequestPermission;
import com.longrise.android.photowall.Album;
import com.longrise.android.result.ActivityResult;
import com.longrise.android.result.IActivityOnResultListener;
import com.longrise.android.web.R;
import com.longrise.android.web.WebLog;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 * 负责响应 HTML 中符合 W3C 的动作语义，例如相册、拍照等
 */
public final class FileChooser<T extends IBridgeAgent<T>> implements IActivityOnResultListener {

    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<Uri[]> mFilePathCallback;

    private final T mTarget;

    public FileChooser(T target) {
        this.mTarget = target;
    }

    @Override
    public final void onActivityResult(int resultCode, Intent data) {
        if (data == null) {
            onReceiveValueEnd();
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (mUploadFile != null) {
                mUploadFile.onReceiveValue(resultCode == Activity.RESULT_OK ? data.getData() : null);
                mUploadFile = null;
            }
        } else {
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                mFilePathCallback = null;
            }
        }
    }

    /**
     * For Android Version < 5.0  todo 4.4 版本是个bug，系统无回调
     */
    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        onReceiveValueEnd();
        this.mUploadFile = uploadFile;

//        final Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
//        chooser.addCategory(Intent.CATEGORY_OPENABLE);
//        chooser.setType(acceptType);

        if (mTarget != null) {
            try {
//                ActivityResult.from(getActivity())
//                        .onResult(this).to(Intent.createChooser(chooser, "File Browser"));
                FileHelper.showFileChooser(getActivity(), this);
            } catch (Exception e) {
                onReceiveValueEnd();
                WebLog.print(e);
            }
        }
    }

    /**
     * For Android Version >= 5.0
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onShowFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        onReceiveValueEnd();
        this.mFilePathCallback = filePathCallback;
//        final Intent chooser = fileChooserParams.createIntent();
        if (mTarget != null) {
            try {
//                ActivityResult.from(getActivity())
//                        .onResult(this).to(chooser);
                FileHelper.showFileChooser(getActivity(), this);
            } catch (Exception e) {
                onReceiveValueEnd();
                WebLog.print(e);
            }
        }
    }

    private void onReceiveValueEnd() {
        if (mUploadFile != null) {
            mUploadFile.onReceiveValue(null);
            mUploadFile = null;
        }
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    private FragmentActivity getActivity() {
        return BridgeApi.getCurrentActivity(mTarget);
    }

    static final class FileHelper {

        /**
         * todo 暂时写死的方式
         */
        static void showFileChooser(final Activity cxt, final FileChooser<?> onResultListener) {
            final String[] items = cxt.getResources().getStringArray(R.array.web_string_file_chooser);

            new AlertDialog.Builder(cxt)
                    .setTitle(R.string.web_string_file_chooser_title)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: // 手机相册
                                    Album.galleryOf().start(cxt, onResultListener);
                                    break;

                                case 1: // 相机拍照
                                    RequestPermission.of((FragmentActivity) cxt).onPermissionResult(new IPermissionHelper() {
                                        @Override
                                        protected boolean onPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults) {
                                            if (isGranted()) {
                                                Album.takeOf(new Album.ITakeListener() {
                                                    @Override
                                                    public void onTaken(@Nullable Uri uri) {
                                                        final Intent intent = new Intent();
                                                        intent.setData(uri);
                                                        onResultListener.onActivityResult(Activity.RESULT_OK, intent);
                                                    }
                                                }).start(cxt);
                                            }else{
                                                onResultListener.onReceiveValueEnd();
                                            }
                                            return false;
                                        }
                                    }).request(Manifest.permission.CAMERA);
                                    break;

                                case 2: // 文件管理器
                                    final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.setType("*/*");
                                    ActivityResult.from((FragmentActivity) cxt).onResult(onResultListener).to(intent);
                                    break;

                                default:
                                    break;
                            }
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    onResultListener.onReceiveValueEnd();
                }
            }).create().show();
        }
    }
}
