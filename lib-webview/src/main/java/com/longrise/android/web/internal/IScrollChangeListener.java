package com.longrise.android.web.internal;

/**
 * Created by godliness on 2019-07-17.
 *
 * @author godliness
 */
public interface IScrollChangeListener {

    /**
     * 监听 WebView 滑动
     *
     * @param left    Current horizontal scroll origin.
     * @param top     Current vertical scroll origin.
     * @param oldLeft Previous horizontal scroll origin.
     * @param oldTop  Previous vertical scroll origin.
     */
    void onScroll(int left, int top, int oldLeft, int oldTop);

    /**
     * 监听 WebView 滑动到顶部
     *
     * @param left    Current horizontal scroll origin.
     * @param top     Current vertical scroll origin.
     * @param oldLeft Previous horizontal scroll origin.
     * @param oldTop  Previous vertical scroll origin.
     */
    void onScrollTop(int left, int top, int oldLeft, int oldTop);

    /**
     * 监听 WebView 滑动到底部
     *
     * @param left    Current horizontal scroll origin.
     * @param top     Current vertical scroll origin.
     * @param oldLeft Previous horizontal scroll origin.
     * @param oldTop  Previous vertical scroll origin.
     */
    void onScrollEnd(int left, int top, int oldLeft, int oldTop);
}
