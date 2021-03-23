package com.longrise.android.permission.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.longrise.android.permission.R;

import java.lang.ref.WeakReference;

/**
 * Created by godliness on 2020/9/29.
 *
 * @author godliness
 */
public final class PermissionDialog extends DialogFragment implements IPermissionDialog<PermissionDialog>, View.OnClickListener {

    private static final String KEY = "permission-dialog";

    private TextView mTitle;
    private TextView mContent;
    private TextView mGuide;

    private WeakReference<FragmentActivity> mHost;
    private Builder mBuilder;

    private DialogInterface.OnCancelListener mCancelListener;
    private DialogInterface.OnDismissListener mDismissListener;
    private OnPermissionListener mPermissionListener;

    @Override
    public void show() {
        show(mHost.get().getSupportFragmentManager(), KEY);
    }

    @Override
    public PermissionDialog setOnCancelListener(DialogInterface.OnCancelListener cancelListener) {
        this.mCancelListener = cancelListener;
        return this;
    }

    @Override
    public PermissionDialog setOnDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.mDismissListener = dismissListener;
        return this;
    }

    @Override
    public PermissionDialog setOnPermissionListener(OnPermissionListener permissionListener) {
        this.mPermissionListener = permissionListener;
        return this;
    }

    public static Builder of(FragmentActivity host) {
        return new Builder(host);
    }

    PermissionDialog() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_permission_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mTitle = view.findViewById(R.id.tv_title_permission);
        mContent = view.findViewById(R.id.tv_content_permission);
        mGuide = view.findViewById(R.id.tv_guide_permission);
        bindMessage();

        view.findViewById(R.id.tv_cancel_permission).setOnClickListener(this);
        view.findViewById(R.id.tv_confirm_permission).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCancelable(mBuilder.mCancelable);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mCancelListener != null) {
            mCancelListener.onCancel(dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.tv_cancel_permission) {
            dismiss();
            if (mPermissionListener != null) {
                mPermissionListener.onCancel();
            }
        } else if (id == R.id.tv_confirm_permission) {
            dismiss();
            Util.toAppInfo(mHost.get());
            if (mPermissionListener != null) {
                mPermissionListener.onConfirm();
            }
        }
    }

    PermissionDialog show(Builder builder, FragmentActivity host) {
        bind(builder, host);
        show(host.getSupportFragmentManager(), KEY);
        return this;
    }

    PermissionDialog create(Builder builder, FragmentActivity host) {
        bind(builder, host);
        return this;
    }

    private void bind(Builder builder, FragmentActivity host) {
        this.mBuilder = builder;
        this.mHost = new WeakReference<>(host);
    }

    private void bindMessage() {
        mTitle.setText(mBuilder.mTitle);
        mContent.setText(mBuilder.mContent);
        mGuide.setText(mBuilder.mGuide);
    }
}
