package com.longrise.android.web.internal.filer;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.longrise.android.web.R;
import com.longrise.android.web.internal.filer.accept.AcceptHelper;
import com.longrise.android.web.internal.filer.accept.Accept;

import java.util.List;

/**
 * Created by godliness on 2020/11/20.
 *
 * @author godliness
 * 辅助 H5 中 input 标签完成文件选择
 */
final class FilerHelper {

    static void openFileChooser(FragmentActivity cxt, String accept, IFilerListener filerListener) {
        final String[] accepts;
        if (!TextUtils.isEmpty(accept)) {
            if (accept.indexOf(",") > 0) {
                accepts = accept.split(",");
            } else {
                accepts = new String[]{accept};
            }
        } else {
            accepts = new String[]{""};
        }
        onShowFileChooser(cxt, accepts, filerListener);
    }

    static void onShowFileChooser(FragmentActivity cxt, String[] accepts, IFilerListener filerListener) {
        if (accepts != null && accepts.length == 1) {
            final String accept = accepts[0];
            if (accept.indexOf(",") > 0) {
                accepts = accept.split(",");
            }
        }
        showFiler(cxt, accepts, filerListener);
    }

    private static void showFiler(FragmentActivity cxt, String[] items, final IFilerListener onResultListener) {
        final List<Accept> accepts = AcceptHelper.createAccepts(cxt, items);
        if (accepts == null) {
            return;
        }
        new AlertDialog.Builder(cxt)
                .setTitle(R.string.web_string_file_chooser_title)
                .setItems(convertItems(accepts), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        accepts.get(which).onClick(onResultListener);
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onResultListener.onReceiveValueEnd();
            }
        }).create().show();
    }

    private static String[] convertItems(List<Accept> accepts) {
        final String[] items = new String[accepts.size()];
        int i = -1;
        for (Accept accept : accepts) {
            items[++i] = accept.getName();
        }
        return items;
    }
}
