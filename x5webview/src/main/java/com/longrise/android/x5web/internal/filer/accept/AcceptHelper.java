package com.longrise.android.x5web.internal.filer.accept;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by godliness on 2020/11/20.
 *
 * @author godliness
 */
public final class AcceptHelper {

    @Nullable
    public static List<Accept> createAccepts(FragmentActivity cxt, String[] accepts) {
        if (accepts == null || accepts.length <= 0) {
            return null;
        }
        final List<Accept> acceptList = new ArrayList<>(accepts.length);
        LOOP:
        for (String accept : accepts) {
            final int index = accept.indexOf("/");
            if (index <= 0) {
                continue;
            }
            final String type = accept.substring(0, index);
            switch (type) {
                case "*":  // all
                    acceptList.clear();
                    addAlbum(acceptList, cxt, "image/*");
                    addVideo(acceptList, cxt, "video/*");
                    addAudio(acceptList, cxt, "audio/*");
                    break LOOP;

                case "image": // 手机相册、拍照
                    addAlbum(acceptList, cxt, type);
                    break;

                case "audio": // 手机音频、录音
                    addAudio(acceptList, cxt, type);
                    break;

                case "video": // 手机视频、录像
                    addVideo(acceptList, cxt, type);
                    break;

                default:
                    break;
            }
        }
        acceptList.add(new Accept.FilerAccept(cxt, "*/*"));
        return acceptList;
    }

    private static void addAlbum(List<Accept> acceptList, FragmentActivity cxt, String type) {
        acceptList.add(new Accept.AlbumAccept(cxt, type));
        acceptList.add(new Accept.CameraAccept(cxt, type));
    }

    private static void addAudio(List<Accept> acceptList, FragmentActivity cxt, String type) {
        acceptList.add(new Accept.Audio(cxt, type));
        acceptList.add(new Accept.AudioRecorderAccept(cxt, type));
    }

    private static void addVideo(List<Accept> acceptList, FragmentActivity cxt, String type) {
        acceptList.add(new Accept.VideoAccept(cxt, type));
        acceptList.add(new Accept.VideoRecorderAccept(cxt, type));
    }
}
