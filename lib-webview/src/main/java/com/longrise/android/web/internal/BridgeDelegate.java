package com.longrise.android.web.internal;

/**
 * Created by godliness on 2020/9/4.
 *
 * @author godliness
 */
final class BridgeDelegate implements IBridgeListener {

    private static final int LOAD_FAILED = 0;
    private static final int LOAD_COMPLETED = 1;

    private IWebLoadListener mLoadCallback;

    private boolean mLoadFailed;
    private int mLoadActor;
    private int mCurrentLoadStatus = -1;

    @Override
    public boolean beforeUrlLoading(String url) {
        if (mLoadCallback != null) {
            return mLoadCallback.shouldOverrideUrlLoading(url);
        }
        return false;
    }

    @Override
    public void onProgressChanged(int newProgress) {
        if (mLoadCallback != null) {
            mLoadCallback.onProgressChanged(newProgress);
        }

        caleLoadActor(newProgress);

        if (mLoadActor >= 2 && mLoadFailed) {
            notifyLoadCompleted();
        }
    }

    @Override
    public void onReceivedTitle(String title) {
        if (mLoadCallback != null) {
            mLoadCallback.onReceivedTitle(title);
        }
    }

    @Override
    public void onPageStarted() {
        this.mLoadActor = 0;
        this.mLoadFailed = false;
    }

    @Override
    public void onPageFinished() {
        if (mLoadFailed) {
            if (mLoadActor >= 2) {
                notifyLoadCompleted();
            } else {
                notifyLoadFailed();
            }
        } else {
            notifyLoadCompleted();
        }
    }

    @Override
    public void onReceivedError() {
        this.mLoadFailed = true;
    }

    @Override
    public void registerCallback(IWebLoadListener webCallback) {
        this.mLoadCallback = webCallback;
    }

    @Override
    public void destroy() {
        mLoadCallback = null;
        mCurrentLoadStatus = -1;
    }

    static IBridgeListener getInstance() {
        return new BridgeDelegate();
    }

    private void notifyLoadFailed() {
        if (mCurrentLoadStatus == LOAD_FAILED) {
            return;
        }
        if (mLoadCallback != null) {
            mLoadCallback.loadFailed();
        }
        this.mCurrentLoadStatus = LOAD_FAILED;
    }

    private void notifyLoadCompleted() {
        if (mCurrentLoadStatus == LOAD_COMPLETED) {
            return;
        }
        if (mLoadCallback != null) {
            mLoadCallback.loadSucceed();
        }
        this.mCurrentLoadStatus = LOAD_COMPLETED;
    }

    private void caleLoadActor(int progress) {
        if (progress >= 50) {
            if (progress <= 60) {
                mLoadActor++;
            } else if (progress <= 70) {
                mLoadActor++;
            } else if (progress <= 80) {
                mLoadActor++;
            } else if (progress <= 90) {
                mLoadActor++;
            }
        }
    }

    private BridgeDelegate() {

    }
}
