package com.longrise.android.photowall.album;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.PopupWindow;

import com.longrise.android.photowall.R;

import java.util.List;

/**
 * Created by godliness on 2020/9/8.
 *
 * @author godliness
 */
final class FolderWindow extends PopupWindow implements Runnable, FolderAdapter.OnFolderClickListener {

    private Context mCxt;
    private final LayoutInflater mInflater;

    private final ViewGroup mContentView;
    private View mRoot;
    private FolderAdapter mAdapter;

    private int mApplyHeight;
    private OnFolderListener mFolderListener;

    public interface OnFolderListener {

        /**
         * 已经显示
         */
        void onFolderShow();

        /**
         * 已经消失
         */
        void onFolderDismiss();

        /**
         * 目录点击回调
         *
         * @param folder {@link Folder}
         */
        void onFolderClick(Folder folder);
    }

    public FolderWindow(Activity cxt) {
        this.mCxt = cxt;
        this.mInflater = LayoutInflater.from(cxt);
        this.mContentView = (ViewGroup) cxt.getWindow().getDecorView();
        if (cxt instanceof OnFolderListener) {
            setOnFolderClickListener((OnFolderListener) cxt);
        }
        initWindow();
        adjustWindowMaxHeight();
    }

    public void applyMaxHeight(int applyHeight) {
        this.mApplyHeight = applyHeight;
    }

    public void setOnFolderClickListener(OnFolderListener listener) {
        this.mFolderListener = listener;
    }

    public void setData(List<Folder> folders) {
        if (mAdapter != null) {
            mAdapter.setData(folders);
        }
    }

    @Override
    public void dismiss() {
        startWindowAnimator(false, new BaseAnimatorEndListener(this));
    }

    @Override
    public void onFolderClick(Folder folder) {
        dismiss();
        if (mFolderListener != null) {
            mFolderListener.onFolderClick(folder);
        }
    }

    @Override
    public void run() {
        startWindowAnimator(true, null);
        final int height = getContentHeight();

        if (height > mApplyHeight) {
            final ViewGroup.LayoutParams params = getContentView().getLayoutParams();
            params.height = mApplyHeight;
        } else {
            if (mFolderListener != null) {
                mFolderListener.onFolderShow();
            }
        }
    }

    void superDismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mFolderListener != null) {
            mFolderListener.onFolderDismiss();
        }
    }

    private void initWindow() {
        setContentView(mRoot = mInflater.inflate(R.layout.window_folder, mContentView, false));
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0));

        initView();
    }

    private void initView() {
        final RecyclerView rcv = findViewById(R.id.rv_folder);
        mAdapter = new FolderAdapter(mCxt);
        mAdapter.setFolderClickListener(this);
        rcv.setLayoutManager(new LinearLayoutManager(mCxt));
        rcv.setAdapter(mAdapter);
    }

    private <T extends View> T findViewById(@IdRes int id) {
        return mRoot.findViewById(id);
    }

    private void adjustWindowMaxHeight() {
        mRoot.removeCallbacks(this);
        mRoot.post(this);
    }

    private int getContentHeight() {
        return getContentView().getHeight();
    }

    private void startWindowAnimator(boolean enter, Animator.AnimatorListener animatorListener) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(getContentView(), "alpha", enter ? 0 : 1, enter ? 1 : 0.5F);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(getContentView(), "translationY", enter ? getContentHeight() : 0, enter ? 0 : getContentHeight());
        AnimatorSet set = new AnimatorSet();
        set.setDuration(200);
        set.playTogether(alpha, translationY);
//        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setInterpolator(new LinearInterpolator());
        if (animatorListener != null) {
            set.addListener(animatorListener);
        }
        set.start();
    }
}
