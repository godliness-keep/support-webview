package com.longrise.android.photowall.album;

import android.text.TextUtils;

import com.longrise.android.photowall.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godliness on 16/3/20.
 *
 * @author godliness
 */
final class Folder {

    private String mFolderName;
    //    private String mFolderDir;
    private String mFirstFilePath;
    private int mCount;
    private final ArrayList<String> mFiles;

    /**
     * 获取目录下第一个文件路径
     */
    String getFirstFilePath() {
        return mFirstFilePath;
    }

    /**
     * 获取该目录名称
     */
    String getFolderName() {
        return mFolderName;
    }

    Folder() {
        this.mFiles = new ArrayList<>(32);
    }

    /**
     * 获取该目录下文件总数
     */
    int getCount() {
//        if (mCount <= 0) {
//            calcFileInFolder();
//        }
        if (mFiles != null) {
            mCount = mFiles.size();
        }
        return mCount;
    }

    /**
     * 过去该目录下文件集合
     */
    List<String> getFiles() {
//        if (mFiles == null) {
//            calcFileInFolder();
//        }
        return mFiles;
    }

    void setFolderDir(String folderDir) {
//        this.mFolderDir = folderDir;
        if (folderDir != null) {
            this.mFolderName = matchAlreadyName(folderDir);
        }
    }

    void addFile(String file) {
        mFiles.add(file);
    }

    void setFirstFilePath(String mFirstFilePath) {
        this.mFirstFilePath = mFirstFilePath;
    }

    void trimToSize() {
        mFiles.trimToSize();
    }

//    private void calcFileInFolder() {
//        final String[] files = new File(mFolderDir).list(FilenameAccept.getFilenameFilter());
//        this.mCount = files.length;
//        this.mFiles = new ArrayList<>(mCount);
//        final String path = new File(mFolderDir).getAbsolutePath();
//        for (String s : files) {
//            mFiles.add(path + "/" + s);
//        }
//    }

    private String matchAlreadyName(String folderDir) {
        final int lastIndexOf = folderDir.lastIndexOf("/");
        final String folderName = folderDir.substring(lastIndexOf + 1);
        if (TextUtils.equals(folderName, Utils.getString(R.string.lib_album_string_news_article_key))) {
            return Utils.getString(R.string.lib_album_string_news_article);
        } else if (TextUtils.equals(folderName, Utils.getString(R.string.lib_album_string_screenshot_key))) {
            return Utils.getString(R.string.lib_album_string_screenshot);
        }
        return folderName;
    }
}

