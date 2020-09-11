package com.longrise.android.webview.demo.x5demo;

import android.os.Message;

import com.longrise.android.x5web.X5;
import com.longrise.android.x5web.internal.bridge.BaseDownloader;

/**
 * Created by godliness on 2020/9/2.
 *
 * @author godliness
 */
public final class DemoDownloader extends BaseDownloader<WebX5DemoActivity> {

    private static final String TAG = "DemoDownloader";

    /**
     * Notify the host application that a file should be downloaded
     *
     * @param url                The full url to the content that should be downloaded
     * @param userAgent          the user agent to be used for the download.
     * @param contentDisposition Content-disposition http header, if
     *                           present.
     * @param mimetype           The mimetype of the content reported by the server
     * @param contentLength      The file size reported by the server
     */
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        X5.error(TAG, "onDownloadStart: " + url);
    }

    @Override
    public boolean onHandleMessage(Message msg) {
        X5.error(TAG, "收到...");
        return false;
    }

    /**
     * 对应{@link Activity#onDestroy()}
     */
    @Override
    public void onDestroy() {

    }
}
