package com.longrise.android.compattoast;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.SoftReference;

/**
 * Created by godliness on 2020/10/14.
 *
 * @author godliness
 */
public final class DialogCompat extends DialogFragment {

    private DialogInterface.OnKeyListener mOnKeyListener;

    private static DialogCompat sModal;
    private final TH mTH;

    private String mTitle;
    private int mIcon;
    private int mDuration;
    private int mLayoutResourceId;

    /**
     * 作为模态化的 Toast 提示
     */
    public static void showToast(FragmentManager fm, String title, int duration, @DrawableRes int icon) {
        if (sModal == null) {
            new DialogCompat().params(title, duration, icon).matchLayoutResource(true).show(fm);
        }
    }

    /**
     * 作为模态化的 Loading 提示
     */
    public static void showLoading(FragmentManager fm, String title) {
        if (sModal == null) {
            new DialogCompat().params(title, TipsManager.INFINITY, TipsManager.INFINITY).matchLayoutResource(false).show(fm);
        }
    }

    /**
     * 关闭模态化提示
     */
    public static void hide() {
        if (sModal != null) {
            sModal.dismiss();
        }
    }

    /**
     * 是否 showing
     */
    public static boolean isShowing() {
        return sModal != null;
    }

    public DialogCompat() {
        this.mTH = new TH(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(mLayoutResourceId, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TextView title = findViewById(R.id.tv_toast);
        if (title != null) {
            title.setText(mTitle);
        }

        final ImageView icon = findViewById(R.id.iv_toast);
        if (icon != null) {
            icon.setImageResource(mIcon);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(getOnKeyListener());
        final Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setDimAmount(0f);
        }
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                throw e;
            }
        } finally {
            if (mDuration != TipsManager.INFINITY) {
                mTH.hide(mDuration);
            }
            sModal = this;
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                throw e;
            }
        } finally {
            sModal = null;
        }
    }

    private DialogCompat params(String title, int duration, @DrawableRes int icon) {
        this.mTitle = title;
        this.mIcon = icon;
        this.mDuration = duration;
        return this;
    }

    private TH matchLayoutResource(boolean isToast) {
        if (isToast) {
            mLayoutResourceId = mIcon == TipsManager.INFINITY ? R.layout.toast_layout_default_view : R.layout.toast_layout_icon_view;
        } else {
            mLayoutResourceId = R.layout.toast_layout_loading;
        }
        return mTH;
    }

    private static class TH extends Handler {

        private static final String KEY = "dialog_compat";
        private static final int HIDE = 0;
        private static final int SHOW = 1;

        private SoftReference<DialogCompat> mDialog;

        TH(DialogCompat fragment) {
            mDialog = new SoftReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE:
                    final DialogCompat compat = mDialog.get();
                    if (compat != null) {
                        compat.dismiss();
                    }
                    break;

                case SHOW:
                    final DialogCompat dialogCompat = mDialog.get();
                    if (dialogCompat != null) {
                        dialogCompat.show((FragmentManager) msg.obj, KEY);
                    }
                    break;

                default:
                    break;
            }
        }

        void hide(int duration) {
            sendEmptyMessageDelayed(HIDE, duration);
        }

        void show(FragmentManager manager) {
            final Message show = obtainMessage(SHOW);
            show.obj = manager;
            show.sendToTarget();
        }
    }

    private DialogInterface.OnKeyListener getOnKeyListener() {
        if (mOnKeyListener == null) {
            mOnKeyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    return keyCode == KeyEvent.KEYCODE_BACK;
                }
            };
        }
        return mOnKeyListener;
    }

    private <V extends View> V findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }
}
