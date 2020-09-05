package com.longrise.android.web.internal;

import com.longrise.android.web.WebLog;
import com.longrise.android.web.internal.webcallback.WebLoadListener;

/**
 * Created by godliness on 2020/9/4.
 *
 * @author godliness
 */
public final class ClientBridgeAgent {

    private static final String TAG = "ClientBridgeAgent";

    private static final byte LOAD_FAILED = 0;
    private static final byte LOAD_COMPLETED = 1;

    private WebLoadListener mLoadCallback;

    private boolean mLoadFailed;
    private byte mLoadActor;
    private byte mCurrentLoadStatus = -1;

    public boolean beforeUrlLoading(String url) {
        if (mLoadCallback != null) {
            return mLoadCallback.shouldOverrideUrlLoading(url);
        }
        return false;
    }

    public void onProgressChanged(int newProgress) {
        WebLog.error(TAG, "newProgress: " + newProgress);

        if (mLoadCallback != null) {
            mLoadCallback.onProgressChanged(newProgress);
        }

        caleLoadActor(newProgress);

        if (mLoadActor >= 2 && mLoadFailed) {
            notifyLoadCompleted();
        }
    }

    public void onReceivedTitle(String title) {
        if (mLoadCallback != null) {
            mLoadCallback.onReceivedTitle(title);
        }
    }

    public void onPageStarted() {
        this.mLoadActor = 0;
        this.mLoadFailed = false;
    }

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

    public void onReceivedError() {
        this.mLoadFailed = true;
    }

    public void registerCallback(WebLoadListener webCallback) {
        this.mLoadCallback = webCallback;
    }

    static ClientBridgeAgent getInstance() {
        return new ClientBridgeAgent();
    }

    void destroy() {
        mLoadCallback = null;
        mCurrentLoadStatus = -1;
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

    private ClientBridgeAgent() {

    }
}
