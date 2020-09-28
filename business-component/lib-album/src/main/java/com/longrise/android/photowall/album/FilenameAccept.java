package com.longrise.android.photowall.album;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by godliness on 2020/9/8.
 *
 * @author godliness
 */
final class FilenameAccept {

    private interface Type {
        String JPG = ".jpg";
        String JPEG = ".jpeg";
        String PNG = ".png";
    }

    static FilenameFilter getFilenameFilter() {
        return Holder.FILENAME_ACCEPT.mFilenameFilter;
    }

    private final FilenameFilter mFilenameFilter;

    private FilenameAccept() {
        this.mFilenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(Type.JPG) || name.endsWith(Type.JPEG) || name.endsWith(Type.PNG);
            }
        };
    }

    private static class Holder {
        private static final FilenameAccept FILENAME_ACCEPT = new FilenameAccept();
    }
}
