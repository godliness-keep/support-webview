package com.longrise.android.webview.demo.x5demo;

import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.smtt.sdk.TbsReaderView;

/**
 * Created by godliness on 2021/1/18.
 *
 * @author godliness
 * todo 最佳的做法还是应该 HOOK 到事件点，拦截获取分发的进度值，不过暂时没有找到这个点
 */
public final class TbsReaderWizard {

    private static final String TAG = "TbsReaderWizard";

    private ArraySet<Object> mFailed;

    public interface OnPageChangeListener {

        void onPageChange(int current, int tatal);
    }

    private final TbsReaderView mReaderView;
    private final OnPageChangeListener mCallback;

    private TextView mTarget;
    private int mTotal = -1;
    private String mLast;

    public TbsReaderWizard(TbsReaderView readerView, OnPageChangeListener callback) {
        this.mReaderView = readerView;
        this.mCallback = callback;
    }

    public void onCallBackAction() {
        if (mTarget == null) {
            findProgressView();
            if (mTarget != null) {
                mTarget.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        final String afterValue = s != null ? s.toString() : "";
                        if (TextUtils.equals(afterValue, mLast)) {
                            // 它本质还是计算 WebView 的滑动进度
                            // 由于具有滑动范围，所以会出现 current 多次回调的场景
                            // 拦截
                            return;
                        }
                        if (TextUtils.isEmpty(afterValue)) {
                            // 这个不一定找错了
                            return;
                        }
                        mLast = afterValue;
                        calcParseResult(afterValue);
                    }
                });
            }
        }
    }

    private void calcParseResult(String value) {
        final String[] args = value.split("/");
        if (args.length == 2) {
            int current = 0;
            try {
                current = Integer.parseInt(args[0]);
                if (mTotal <= 0) {
                    mTotal = Integer.parseInt(args[1]);
                }
            } catch (Exception e) {
                // 找错了
                foundError();
            } finally {
                if (mCallback != null) {
                    mCallback.onPageChange(current, mTotal);
                }
            }
        } else {
            foundError();
        }
    }

    private void findProgressView() {
        final int childSize = mReaderView.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = mReaderView.getChildAt(i);
            if (child instanceof ViewGroup) {
                final TextView findResult = recursiveSearch2((ViewGroup) child);
                if (findResult != null) {
                    mTarget = findResult;
                    break;
                }
            } else if (child instanceof TextView) {
                // 只需要关心是 TextView 即可
                if (checkRule((TextView) child)) {
                    mTarget = (TextView) child;
                    break;
                }
            }
        }
    }

    private TextView recursiveSearch2(ViewGroup group) {
        Log.e(TAG, "child: " + group.getClass().getName());
        TextView find = null;
        final int childSize = group.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = group.getChildAt(i);
            Log.e(TAG, "child: " + child.getClass().getName());
            if (child instanceof ViewGroup) {
                find = recursiveSearch2((ViewGroup) child);
                if (find != null) {
                    break;
                }
            } else if (child instanceof TextView) {
                if (checkRule((TextView) child)) {
                    find = (TextView) child;
                    break;
                }
            }
        }
        return find;
    }

    private String getValue() {
        final CharSequence s = mTarget.getText();
        if (s != null) {
            return s.toString();
        }
        return "";
    }

    private boolean checkRule(@NonNull TextView child) {
        final CharSequence s = child.getText();
        // 简单校验即可确定是否是需要的内容
        return checkFailedList(child) && (s != null && s.toString().indexOf("/") > 0);
    }

    private boolean checkFailedList(@NonNull TextView child) {
        return mFailed == null || !mFailed.contains(child);
    }

    private void addFailed(Object failed) {
        if (mFailed == null) {
            mFailed = new ArraySet<>(3);
        }
        mFailed.add(failed);
    }

    private void foundError() {
        addFailed(mTarget);
        mTarget = null;
    }
}
