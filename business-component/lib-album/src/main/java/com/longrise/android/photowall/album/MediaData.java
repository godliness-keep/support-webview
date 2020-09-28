package com.longrise.android.photowall.album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godliness on 2020/9/8.
 *
 * @author godliness
 */
final class MediaData {

    private final List<Folder> mFolders = new ArrayList<>();
    private final List<String> mTotalFiles;

    MediaData(int fileSize) {
        this.mTotalFiles = new ArrayList<>(fileSize);
    }

    void addFolder(Folder folder) {
        mFolders.add(folder);
    }

    List<Folder> getFolders() {
        return mFolders;
    }

    int getFolderSize() {
        return mFolders.size();
    }

    List<String> getTotalFiles() {
        return mTotalFiles;
    }

    int getFileSize() {
        return mTotalFiles.size();
    }

    void addFile(String path) {
        mTotalFiles.add(path);
    }

    void trimToSize() {
        for (Folder folder : mFolders) {
            folder.trimToSize();
        }
    }
}
