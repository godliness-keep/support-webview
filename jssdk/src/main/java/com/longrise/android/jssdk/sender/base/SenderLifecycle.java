package com.longrise.android.jssdk.sender.base;

import android.view.View;

/**
 * Created by godliness on 2020-04-20.
 *
 * @author godliness
 */
final class SenderLifecycle implements View.OnAttachStateChangeListener {

    private final SendersManager mMangaer;

    SenderLifecycle(SendersManager manager2) {
        this.mMangaer = manager2;
    }

    void registerSenderLifecycle(View host) {
        host.removeOnAttachStateChangeListener(this);
        host.addOnAttachStateChangeListener(this);
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        v.removeOnAttachStateChangeListener(this);
        mMangaer.onViewDetachedFromWindow(v.hashCode());
    }
}
