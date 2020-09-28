package com.longrise.android.photowall;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by godliness on 2020/9/15.
 *
 * @author godliness
 */
class Utils {

    private static final String ALBUM = "album";

    static Uri getLocalUri(Context cxt, String name) {
        return Uri.fromFile(getLocalFile(cxt, name));
    }

    static File getLocalFile(Context cxt, String name) {
        final File tempFile = new File(getTempFile(cxt), name);
        if (tempFile.exists()) {
            final boolean delete = tempFile.delete();

            if (BuildConfig.DEBUG) {
                Log.e(ALBUM, "delete " + name + " " + delete);
            }
        }
        return tempFile;
    }

    static Uri transformProviderUri(@NonNull Context cxt, @NonNull File file) {
        final Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(cxt.getApplicationContext(), cxt.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    @NonNull
    static File getTempFile(Context cxt) {
        if (checkSdCard()) {
            final File file = cxt.getApplicationContext().getExternalFilesDir(ALBUM);
            if (file != null) {
                return file;
            }
        }
        return new File(cxt.getApplicationContext().getFilesDir(), ALBUM);
    }

    private static boolean checkSdCard() {
        return TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED);
    }
}
