package com.longrise.android.photowall.album;

import android.animation.Animator;

/**
 * Created by godliness on 2020/9/8.
 *
 * @author godliness
 */
final class BaseAnimatorEndListener implements Animator.AnimatorListener {

    private final FolderWindow mWindow;

    BaseAnimatorEndListener(FolderWindow window){
        this.mWindow = window;
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mWindow.superDismiss();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
