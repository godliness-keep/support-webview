package com.longrise.android.photowall.filer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.longrise.android.photowall.preview.PreviewActivity;

/**
 * Created by godliness on 2020/9/12.
 *
 * @author godliness
 * 照片预览
 */
public final class Preview {

    private String[] mValues;

    public Preview(String current, @Nullable String[] values) {
        if (TextUtils.isEmpty(current)) {
            throw new NullPointerException("Current path must not be null");
        }

        final String[] newUrls;
        if (values == null) {
            newUrls = new String[]{current};
        } else {
            newUrls = new String[values.length + 1];
            newUrls[0] = current;
            System.arraycopy(values, 0, newUrls, 1, values.length);
        }
        this.mValues = newUrls;
    }

    public void start(@NonNull Context target) {
        final Intent intent = new Intent(target, PreviewActivity.class);
        intent.putExtra(PreviewActivity.EXTRA_URLS, mValues);
        target.startActivity(intent);
    }
}
