package com.longrise.android.permission;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020/9/29.
 *
 * @author godliness
 */
public final class PermissionDialog extends DialogFragment implements View.OnClickListener {

    private static final String KEY = "permission-dialog";

    interface Extra {
        String TITLE = "per-title";
        String CONTENT = "per-content";
        String GUIDE = "per-guide";
        String TOUCH_OUT = "per-touch-out";
    }

    public interface IPermissionListener {

        void onCancel();
    }

    private TextView mTitle;
    private TextView mContent;
    private TextView mGuide;

    private final Bundle mExtra;
    private IPermissionListener mCallback;
    private WeakReference<FragmentActivity> mHost;

    public PermissionDialog() {
        mExtra = new Bundle();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        final boolean touchOutside = mExtra.getBoolean(Extra.TOUCH_OUT, true);
        dialog.setCanceledOnTouchOutside(touchOutside);
        if (!touchOutside) {
            dialog.setOnKeyListener(getOnKeyListener());
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_permission_request, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTitle = findViewById(R.id.tv_title_permission);
        mContent = findViewById(R.id.tv_content_permission);
        mGuide = findViewById(R.id.tv_guide_permission);
        bindMessage();

        findViewById(R.id.tv_cancel_permission).setOnClickListener(this);
        findViewById(R.id.tv_confirm_permission).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.tv_cancel_permission) {
            dismiss();
            if (mCallback != null) {
                mCallback.onCancel();
            }
        } else if (id == R.id.tv_confirm_permission) {
            dismiss();
            Util.toAppInfo(mHost.get());
        }
    }

    public static PermissionDialog of(FragmentActivity host) {
        return new PermissionDialog().bindHost(host);
    }

    public PermissionDialog title(String title) {
        mExtra.putString(Extra.TITLE, title);
        return this;
    }

    public PermissionDialog content(String content) {
        mExtra.putString(Extra.CONTENT, content);
        return this;
    }

    public PermissionDialog guide(String guide) {
        mExtra.putString(Extra.GUIDE, guide);
        return this;
    }

    public PermissionDialog callback(IPermissionListener callback) {
        this.mCallback = callback;
        return this;
    }

    public PermissionDialog canceledOnTouchOutside(boolean canceledOnTouchOutside) {
        mExtra.putBoolean(Extra.TOUCH_OUT, canceledOnTouchOutside);
        return this;
    }

    public void show() {
        show(mHost.get().getSupportFragmentManager(), KEY);
    }

    PermissionDialog bindHost(FragmentActivity host) {
        this.mHost = new WeakReference<>(host);
        return this;
    }

    private <V extends View> V findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    private DialogInterface.OnKeyListener getOnKeyListener() {
        return new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        };
    }

    private void bindMessage() {
        mTitle.setText(mExtra.getString(Extra.TITLE));
        mContent.setText(mExtra.getString(Extra.CONTENT));
        mGuide.setText(mExtra.getString(Extra.GUIDE));
    }
}
