package com.longrise.android.web.helper;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;


import com.longrise.android.mvp.utils.MvpLog;

import java.io.File;

/**
 * Created by godliness on 2019-11-19.
 *
 * @author godliness
 */
public final class WebViewDownloadHelper {

    private static final String TAG = "WebViewDownloadHelper";

    private final Context mContext;

    private final ArrayMap<Long, String> mUrls;
    private DownloadCompleteReceiver mDownloadReceiver;

    private WebViewDownloadHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.mUrls = new ArrayMap(5);
        registerDownloadReceiver();
    }

    public static WebViewDownloadHelper installDownloadHelper(Context context) {
        return new WebViewDownloadHelper(context);
    }

    public void uninstallDownloadHelper() {
        if (mDownloadReceiver != null) {
            mContext.unregisterReceiver(mDownloadReceiver);
        }
        mDownloadReceiver = null;
    }

    public boolean isExistLocal(String url) {
        final String fileDir = getCachePath();
        final String name = getDownloadTaskName(url);
        final File file = new File(fileDir, name);
        return file.exists();
    }

    public void addDownloadTask(String url, String disposition) {
        if (url == null) {
            return;
        }
        if (isExistLocal(url)) {
            Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT).show();
            MvpLog.e(TAG, "The resource already exist locally");
            return;
        }
        if (isDownloading(url)) {
            Toast.makeText(mContext, "后台下载中，您可以进行其他操作", Toast.LENGTH_SHORT).show();
            MvpLog.e(TAG, "This url already exist");
            return;
        }
        try {
            mUrls.put(createDownloadTask(url, disposition), url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getMimeType(String filePath) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    private boolean isDownloading(String url) {
        return mUrls.containsValue(url);
    }

    private Long createDownloadTask(String url, String disposition) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDescription(disposition);
        request.setAllowedOverMetered(true);
        request.setVisibleInDownloadsUi(false);
        request.setAllowedOverRoaming(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
        request.setDestinationInExternalFilesDir(mContext, getDirType(), getDownloadTaskName(url));
        final DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        return downloadManager.enqueue(request);
    }

    private String getDownloadTaskName(String url) {
        return Uri.parse(url).getLastPathSegment();
    }

    private String getCachePath() {
        return mContext.getExternalFilesDir(getDirType()).toString();
    }

    private String getDirType(){
        return "WebView";
    }

    private static class DownloadCompleteReceiver extends BroadcastReceiver {

        private final ArrayMap<Long, String> mUrls;

        public DownloadCompleteReceiver(ArrayMap<Long, String> urls) {
            this.mUrls = urls;
        }


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    if (downloadId < 0) {
                        return;
                    }

                    mUrls.remove(downloadId);

                    final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
                    if(TextUtils.isEmpty(type)){
                        type = "*/*";
                    }

                    final Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                    if(uri != null){
                        Intent openUri = new Intent(Intent.ACTION_VIEW);
                        openUri.setDataAndType(uri, type);
                        context.startActivity(openUri);
                    }
                }
            }
        }
    }

    private void registerDownloadReceiver() {
        if (mDownloadReceiver == null) {
            mDownloadReceiver = new DownloadCompleteReceiver(mUrls);
        }
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(mDownloadReceiver, intentFilter);
    }

}
