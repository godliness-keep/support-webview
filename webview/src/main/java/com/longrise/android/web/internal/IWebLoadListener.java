package com.longrise.android.web.internal;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public interface IWebLoadListener {

    /**
     * Current web page title
     *
     * @param title String
     */
    void onReceivedTitle(String title);

    /**
     * Current page loading progress
     *
     * @param newProgress progress
     */
    void onProgressChanged(int newProgress);

    /**
     * Loading the page
     */
    void loadSucceed();

    /**
     * Page loading failed
     */
    void loadFailed();

    /**
     * Unknown uri type
     *
     * @param uri Uri
     * @return If return true, this load is consumed
     */
    boolean shouldOverrideUrlLoading(String uri);
}
