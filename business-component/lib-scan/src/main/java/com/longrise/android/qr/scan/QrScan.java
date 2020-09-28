package com.longrise.android.qr.scan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.longrise.android.result.ActivityResult;
import com.longrise.android.result.IActivityOnResultListener;

/**
 * Created by godliness on 2020/9/16.
 *
 * @author godliness
 */
public final class QrScan {

    public static final String SCAN_RESULT = "scan_result";

    interface Extra {
        String TIP_TEXT = "tip_text";
        String ANIM_TIME = "anim_time";
        String RECT_WIDTH = "rect_width";
        String BAR_CODE = "bar_code";
    }

    private final IScanResultCallback mScanListener;
    private final Bundle mExtra;

    public static QrScan of(@NonNull IScanResultCallback scanResultListener) {
        return new QrScan(scanResultListener);
    }

    /**
     * 扫描框提示语，默认：请将二维码放入扫描框内
     */
    public QrScan withTip(String tips) {
        if (tips != null) {
            this.mExtra.putString(Extra.TIP_TEXT, tips);
        }
        return this;
    }

    /**
     * 是否为条形码，默认 false
     */
    public QrScan withBarCode(boolean isBarCode) {
        this.mExtra.putBoolean(Extra.BAR_CODE, isBarCode);
        return this;
    }

    /**
     * @param duration 动画执行时长，单位 ms，默认 1000ms
     */
    public QrScan withAnimTime(int duration) {
        this.mExtra.putInt(Extra.ANIM_TIME, Math.max(duration, 600));
        return this;
    }

    /**
     * 扫描框的宽度（正方形）
     *
     * @param width 扫描框宽度，单位 px，默认屏幕宽度的 3/4
     */
    public QrScan withWidth(int width) {
        this.mExtra.putInt(Extra.RECT_WIDTH, width);
        return this;
    }

    /**
     * 启动二维码扫描
     */
    public void start(FragmentActivity host) {
        final Intent start = new Intent();
        start.putExtras(mExtra);
        start.setClass(host, QrScanActivity.class);

        ActivityResult.from(host)
                .onResult(new IActivityOnResultListener() {
                    @Override
                    public void onActivityResult(int resultCode, Intent data) {
                        if(mScanListener != null){
                            if (resultCode == Activity.RESULT_OK) {
                                mScanListener.onScanResult(data.getStringExtra(SCAN_RESULT));
                            } else {
                                mScanListener.onScanFailed(data != null);
                            }
                        }
                    }
                }).to(start);
    }

    private QrScan(IScanResultCallback scanResultListener) {
        this.mScanListener = scanResultListener;
        this.mExtra = new Bundle();
    }
}
