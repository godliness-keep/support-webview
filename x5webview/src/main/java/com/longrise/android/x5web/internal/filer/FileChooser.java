package com.longrise.android.x5web.internal.filer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.x5web.X5;
import com.longrise.android.x5web.internal.IBridgeAgent;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 * 负责响应 HTML 中符合 W3C 的动作语义，例如相册、拍照等
 */
public final class FileChooser<T extends IBridgeAgent<T>> implements IFilerListener {

    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<Uri[]> mFilePathCallback;

    private final T mTarget;

    public FileChooser(T target) {
        this.mTarget = target;
    }

    @Override
    public void onActivityResult(int resultCode, Intent data) {
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
                FilerHelper.openFileChooser(getActivity(), acceptType, this);
            } catch (Exception e) {
                onReceiveValueEnd();
                X5.print(e);
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
                FilerHelper.onShowFileChooser(getActivity(), fileChooserParams.getAcceptTypes(), this);
            } catch (Exception e) {
                onReceiveValueEnd();
                X5.print(e);
            }
        }
    }

    @Override
    public void onReceiveValueEnd() {
        if (mUploadFile != null) {
            mUploadFile.onReceiveValue(null);
            mUploadFile = null;
        }
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    private FragmentActivity getActivity(){
        return getCurrentActivity(mTarget);
    }

    private FragmentActivity getCurrentActivity(Object host) {
        final Context cxt = getCurrentContext(host);
        if (cxt instanceof FragmentActivity) {
            return (FragmentActivity) cxt;
        }
        throw new IllegalArgumentException("The bridge`s host must be <? extends FragmentActivity>");
    }

    private Context getCurrentContext(Object host) {
        if (host instanceof FragmentActivity) {
            return (FragmentActivity) host;
        } else if (host instanceof Fragment) {
            final FragmentActivity current = ((Fragment) host).getActivity();
            if (current == null) {
                return ((Fragment) host).getContext();
            }
            return current;
        }
        throw new IllegalArgumentException("The bridge`s host must be <? extends FragmentActivity> || <? extends android.v4.Fragment>");
    }
}
