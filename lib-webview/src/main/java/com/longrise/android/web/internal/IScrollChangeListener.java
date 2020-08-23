package com.longrise.android.web.internal;

/**
 * Created by godliness on 2019-07-17.
 *
 * @author godliness
 */
public interface IScrollChangeListener {

    /**
     * 滑动
     */
    void onScroll(int left, int top, int oldLeft, int oldTop);

    /**
     * 滑动到顶部
     */
    void onScrollTop(int left, int top, int oldLeft, int oldTop);

    /**
     * 滑动到底部
     */
    void onScrollEnd(int left, int top, int oldLeft, int oldTop);
}
