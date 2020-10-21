package com.longrise.android.compattoast;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by godliness on 2020/10/16.
 *
 * @author godliness
 */
public final class Toast implements IToast {

    private final TH mTh;
    private final IToastView mView;

    private int mDuration;

    public static IToast makeToast(String title, int duration) {
        return new Toast().setText(title).setDuration(duration);
    }

    @Override
    public IToast setText(String text) {
        mView.setText(text);
        return this;
    }

    @Override
    public IToast setDuration(int duration) {
        this.mDuration = duration;
        return this;
    }

    @Override
    public IToast setView(View childView) {
        mView.setView(childView);
        return this;
    }

    @Override
    public View getView() {
        return mView.getView();
    }

    @Override
    public IToast setGravity(int gravity, int xOffset, int yOffset) {
        mView.setGravity(gravity, xOffset, yOffset);
        return this;
    }

    public void show(Activity binding) {
        mTh.show(binding, mDuration);
    }

    public Toast() {
        this.mView = new ToastView();
        this.mTh = new TH(mView);
    }

    @Override
    public void cancel() {
        this.mTh.cancel();
    }

    static class TH extends Handler {

        private static final int HIDE = 0;
        private static final int SHOW = 1;

        private final IToastView mView;

        TH(IToastView toastView) {
            this.mView = toastView;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    mView.show((ViewGroup) msg.obj);
                    if (msg.arg1 != -1) {
                        removeMessages(HIDE);
                        sendEmptyMessageDelayed(HIDE, msg.arg1);
                    }
                    break;

                case HIDE:
                    mView.cancel();
                    break;

                default:
                    break;
            }
        }

        void show(Activity binding, int duration) {
            final Message msg = obtainMessage();
            msg.obj = (ViewGroup) binding.findViewById(Window.ID_ANDROID_CONTENT);
            msg.arg1 = duration;
            msg.what = SHOW;
            msg.sendToTarget();
        }

        void cancel() {
            removeMessages(HIDE);
            final Message hide = obtainMessage(HIDE);
            hide.sendToTarget();
        }
    }
}
