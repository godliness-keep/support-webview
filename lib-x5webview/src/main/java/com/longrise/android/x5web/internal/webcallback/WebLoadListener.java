package com.longrise.android.x5web.internal.webcallback;

/**
 * Created by godliness on 2019-07-09.
 *
 * @author godliness
 */
public interface WebLoadListener {

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

//    interface WebChromeListener extends WebCallback{
//
//        /**
//         * Current web page title
//         *
//         * @param title String
//         */
//        void onReceivedTitle(String title);
//
//        /**
//         * Current page loading progress
//         *
//         * @param newProgress progress
//         */
//        void onProgressChanged(int newProgress);
//
//    }
//
//    interface WebViewClientListener extends WebCallback{
//
//        /**
//         * Loading the page
//         */
//        void loadSucceed();
//
//        /**
//         * Page loading failed
//         */
//        void loadFailed();
//
//        /**
//         * Unknown uri type
//         *
//         * @param uri Uri
//         * @return If return true, this load is consumed
//         */
//        boolean shouldOverrideUrlLoading(String uri);
//    }
}
