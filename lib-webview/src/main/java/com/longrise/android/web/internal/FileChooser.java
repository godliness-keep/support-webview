package com.longrise.android.web.internal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import com.longrise.android.web.WebLog;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 * 负责响应 HTML 中符合 W3C 的动作语义，例如相册、拍照等
 */
public final class FileChooser<T extends IBridgeAgent<T>> {

    private static final int REQUEST_CODE_VERSION_LESS_LOLLIPOP = 20;
    private static final int REQUEST_CODE_VERSION_LOLLIPOP = 21;

    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<Uri[]> mFilePathCallback;

    private final T mTarget;

    public FileChooser(T target) {
        this.mTarget = target;
    }

    public final void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
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

        final Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
        chooser.addCategory(Intent.CATEGORY_OPENABLE);
        chooser.setType(acceptType);

        if (mTarget != null) {
            try {
                mTarget.startActivityForResult(Intent.createChooser(chooser, "File Browser"), REQUEST_CODE_VERSION_LESS_LOLLIPOP);
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
        final Intent chooser = fileChooserParams.createIntent();
        if (mTarget != null) {
            try {
                mTarget.startActivityForResult(chooser, REQUEST_CODE_VERSION_LOLLIPOP);
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
}
