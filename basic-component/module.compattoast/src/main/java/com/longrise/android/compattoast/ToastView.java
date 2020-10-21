package com.longrise.android.compattoast;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by godliness on 2020/10/16.
 *
 * @author godliness
 */
final class ToastView implements IToastView {

    private View mChild;
    private FrameLayout.LayoutParams mChildLayoutParams;
    private String mTitle;

    private View mNextView;

    ToastView() {
    }

    @Override
    public IToastView setText(String title) {
        this.mTitle = title;
        return this;
    }

    @Override
    public IToastView setView(View childView) {
        this.mNextView = childView;
        return this;
    }

    @Override
    public View getView() {
        return mChild;
    }

    @Override
    public IToastView setGravity(int gravity, int xOffset, int yOffset) {
        this.mChildLayoutParams = createLayoutParams(gravity, xOffset, yOffset);
        return this;
    }

    @Override
    public void show(ViewGroup parent) {
        this.mChild = mNextView;
        generatorToastView(parent);
        final View old = parent.findViewById(mChild.getId());
        if(old == null){
            parent.addView(mChild, generatorLayoutParams());
        }


//        cancel();
//        this.mChild = mNextView;
//        generatorToastView(parent);
//        final View old = parent.findViewById(mChild.getId());
//        if (old != null) {
//            parent.removeView(old);
//        }
//
//        parent.addView(mChild, generatorLayoutParams());
    }

    @Override
    public void cancel() {
        cancel(mChild);
    }

    private void cancel(View view) {
        if (view != null) {
            final ViewParent vp = view.getParent();
            if (vp instanceof ViewGroup) {
                ((ViewGroup) vp).removeView(view);
            }
        }
    }

    private void generatorToastView(ViewGroup parent) {
        if (mChild != null) {
            return;
        }
        this.mChild = LayoutInflater.from(parent.getContext()).inflate(R.layout.toast_layout_default_view, parent, false);
        if (mTitle != null) {
            ((TextView) mChild.findViewById(R.id.tv_toast)).setText(mTitle);
        }
    }

    private FrameLayout.LayoutParams generatorLayoutParams() {
        if (mChildLayoutParams == null) {
            return createLayoutParams(Gravity.CENTER, 0, 0);
        }
        return mChildLayoutParams;
    }

    private FrameLayout.LayoutParams createLayoutParams(int gravity, int xOffset, int yOffset) {
        final FrameLayout.LayoutParams childLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        childLayoutParams.gravity = gravity;
        childLayoutParams.topMargin = yOffset;
        childLayoutParams.leftMargin = xOffset;
        return childLayoutParams;
    }
}
