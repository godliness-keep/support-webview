package com.longrise.android.jssdk.stream;

import android.webkit.WebView;

import com.longrise.android.jssdk.Response;
import com.longrise.android.jssdk.core.JsCallManager;

/**
 * Created by godliness on 2020-04-23.
 *
 * @author godliness
 */
public final class StreamSender {

    /**
     * 最大允许传递1000KB
     */
    private static final int DEFAULT_CHUNK_LENGTH = 1024 * 1000 / 2;

    public static void sendStream(Response response, WebView target) {
        sendStream(response.toJson(), target);
    }

    public static void sendStream(Response response, int geometry, WebView target) {
        sendStream(response.toJson(), geometry, target);
    }

    public static void sendStream(String stream, WebView target) {
        sendStream(stream, 12, target);
    }

    public static void sendStream(String stream, int geometry, WebView target) {
        final int chunkLength;
        if (geometry != 12) {
            chunkLength = 1024 * geometry / 2;
        } else {
            chunkLength = DEFAULT_CHUNK_LENGTH;
        }
        final int streamSize = stream.length();
        if (streamSize > chunkLength) {
            // 构建分段流，进行发送
            new ChunkStream(stream, stream.length(), chunkLength).sendTo(target);
        } else {
            // 不需要分段传输，直接通过 JsCallManager
            JsCallManager.notifyJavaScriptCallNativeFinished(target, stream);
        }
    }
}
