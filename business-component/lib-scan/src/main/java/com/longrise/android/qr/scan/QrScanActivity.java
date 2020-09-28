package com.longrise.android.qr.scan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.core.ScanBoxView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

/**
 * Created by godliness on 2020/9/16.
 *
 * @author godliness
 * http://p.codekk.com/detail/Android/bingoogolapple/BGAQRCode-Android
 */
public final class QrScanActivity extends AppCompatActivity implements View.OnClickListener, QRCodeView.Delegate {

    private ZBarView mZBar;
    private ScanBoxView mScanBox;

    private String mTips;
    private int mDuration;
    private int mWidth;
    private boolean mBarCode;

    private Intent mScanResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getExtraData(savedInstanceState);
        beforeSetContentView();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_activity_qrscan);
        initView();
    }

    private void initView() {
        mZBar = findViewById(R.id.zbar);
        mZBar.setDelegate(this);
        mScanBox = mZBar.getScanBoxView();
        final View back = findViewById(R.id.iv_back);
        back.setOnClickListener(this);

        final int statusBarHeight = getStatusBarHeight(this);
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) back.getLayoutParams();
        params.topMargin = statusBarHeight;

        configScanBox(statusBarHeight);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        setResult(Activity.RESULT_OK, createScanResult(result));
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        setResult(Activity.RESULT_CANCELED, createScanResult("failed"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mZBar != null) {
            try {
                mZBar.showScanRect();
                mZBar.startSpotDelay(300);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mZBar != null) {
            try {
                mZBar.stopCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mZBar != null) {
            try {
                mZBar.onDestroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getExtraData(Bundle state) {
        if (state == null) {
            getParams(getIntent().getExtras());
        } else {
            getParams(state);
        }
    }

    private void getParams(Bundle extra) {
        if (extra == null) {
            return;
        }
        this.mTips = extra.getString(QrScan.Extra.TIP_TEXT, getString(R.string.scan_string_code_tip));
        this.mDuration = extra.getInt(QrScan.Extra.ANIM_TIME, 1000);
        this.mWidth = extra.getInt(QrScan.Extra.RECT_WIDTH, 0);
        this.mBarCode = extra.getBoolean(QrScan.Extra.BAR_CODE, false);
    }

    private void configScanBox(int statusBarHeight) {
        mScanBox.setTopOffset(calcTopOffset(statusBarHeight));
        mScanBox.setQRCodeTipText(mTips);
        mScanBox.setBarCodeTipText(mTips);
        mScanBox.setAnimTime(mDuration);
        final int calcWidth = calcRectWidth(mWidth);
        mScanBox.setRectWidth(calcWidth);
        mScanBox.setRectHeight(calcWidth);
        mScanBox.setIsBarcode(false);
    }

    private int calcRectWidth(int src) {
        final int width = getResources().getDisplayMetrics().widthPixels;
        if (src > width || src <= 0) {
            return (int) (width * 0.75F);
        }
        return src;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(QrScan.Extra.TIP_TEXT, mTips);
        outState.putInt(QrScan.Extra.ANIM_TIME, mDuration);
        outState.putInt(QrScan.Extra.RECT_WIDTH, mWidth);
        outState.putBoolean(QrScan.Extra.BAR_CODE, mBarCode);
    }

    private void beforeSetContentView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private static int getStatusBarHeight(Context context) {
        final Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    private int calcTopOffset(int statusBarHeight) {
        return (getResources().getDisplayMetrics().heightPixels / 4) + statusBarHeight;
    }

    private Intent createScanResult(String result) {
        if (mScanResult == null) {
            mScanResult = new Intent();
        }
        mScanResult.putExtra(QrScan.SCAN_RESULT, result);
        return mScanResult;
    }
}
