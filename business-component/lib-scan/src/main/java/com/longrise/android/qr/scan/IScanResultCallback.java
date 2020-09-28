package com.longrise.android.qr.scan;

/**
 * Created by godliness on 2020/9/16.
 *
 * @author godliness
 * 扫描结果
 */
public abstract class IScanResultCallback {

    /**
     * 扫描失败
     *
     * @param scanFailed true 未扫描到结果，false 为其他（例如用户取消、退出）
     */
    public void onScanFailed(boolean scanFailed) {
        // todo true 表示扫描失败，false 表示取消扫描
    }

    /**
     * 扫描结果
     *
     * @param result 扫描结果
     */
    public abstract void onScanResult(String result);
}
