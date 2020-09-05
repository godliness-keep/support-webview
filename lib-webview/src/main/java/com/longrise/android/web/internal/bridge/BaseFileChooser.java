package com.longrise.android.web.internal.bridge;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import com.longrise.android.web.BaseWebActivity;
import com.longrise.android.web.WebLog;
import com.longrise.android.web.internal.Internal;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 * 负责响应 HTML 中符合 W3C 的动作语义，例如相册、拍照等
 */
@SuppressWarnings("unused")
public abstract class BaseFileChooser<T extends BaseWebActivity<T>> {

    private static final int REQUEST_CODE_VERSION_LESS_LOLLIPOP = 20;
    private static final int REQUEST_CODE_VERSION_LOLLIPOP = 21;

    private WeakReference<BaseWebActivity<T>> mTarget;
    private Handler mHandler;

    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<Uri[]> mFilePathCallback;

    public BaseFileChooser() {
    }

    /**
     * 通过重写该方法拦截 Activity 的 onActivityResult 事件
     */
    protected abstract boolean dispatchActivityOnResult(int requestCode, int resultCode, Intent data);

    /**
     * 获取当前 Activity
     */
    @SuppressWarnings("unchecked")
    @Nullable
    protected final T getTarget() {
        return (T) mTarget.get();
    }

    /**
     * 当前所依附的 Activity 是否已经 Finished
     */
    protected final boolean isFinished() {
        return Internal.activityIsFinished(getTarget());
    }

    /**
     * 获取 Handler，重写 {@link #onHandleMessage(Message)} 来接消息
     * 注：每个 Web 实例都共用同一个 Handler
     */
    protected final Handler getHandler() {
        return mHandler;
    }

    /**
     * 拦截通过 Handler 发送的消息
     * 注：每个实例都有且共用同一个 Handler 实例
     */
    public boolean onHandleMessage(Message msg) {
        return false;
    }

    /**
     * 利用 Handler 发送任务
     */
    protected final void post(Runnable action) {
        postDelayed(action, 0);
    }

    /**
     * 利用 Handler 发送 Delay 任务
     */
    protected final void postDelayed(Runnable action, int delay) {
        if (!isFinished()) {
            mHandler.postDelayed(action, delay);
        }
    }

    /**
     * 接收 Activity 的 result 事件，重写{@link #dispatchActivityOnResult(int, int, Intent)} 拦截相关事件
     */
    public final void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (isFinished() || data == null || dispatchActivityOnResult(requestCode, resultCode, data)) {
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

    protected final void onReceiveValueEnd() {
        if (mUploadFile != null) {
            mUploadFile.onReceiveValue(null);
            mUploadFile = null;
        }
        if (mFilePathCallback != null) {
            mFilePathCallback.onReceiveValue(null);
            mFilePathCallback = null;
        }
    }

    public final void attachTarget(BaseWebActivity<T> target) {
        this.mTarget = new WeakReference<>(target);
        this.mHandler = target.getHandler();
    }

    /**
     * For Android Version < 5.0  todo 4.4 版本是个bug，系统无回调
     */
    final void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        if (isFinished()) {
            return;
        }
        onReceiveValueEnd();
        this.mUploadFile = uploadFile;

        final Intent chooser = new Intent(Intent.ACTION_GET_CONTENT);
        chooser.addCategory(Intent.CATEGORY_OPENABLE);
        chooser.setType(acceptType);

        final T target = getTarget();
        if (target != null) {
            try {
                target.startActivityForResult(Intent.createChooser(chooser, "File Browser"), REQUEST_CODE_VERSION_LESS_LOLLIPOP);
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
    final void onShowFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        if (isFinished()) {
            return;
        }
        onReceiveValueEnd();
        this.mFilePathCallback = filePathCallback;
        final Intent chooser = fileChooserParams.createIntent();
        final T target = getTarget();
        if (target != null) {
            try {
                target.startActivityForResult(chooser, REQUEST_CODE_VERSION_LOLLIPOP);
            } catch (Exception e) {
                onReceiveValueEnd();
                WebLog.print(e);
            }
        }
    }
}
