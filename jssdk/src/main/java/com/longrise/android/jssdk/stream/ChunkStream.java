package com.longrise.android.jssdk.stream;

import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;

import com.longrise.android.jssdk.BuildConfig;
import com.longrise.android.jssdk.core.JsCallManager;

/**
 * Created by godliness on 2020-05-08.
 *
 * @author godliness
 */
final class ChunkStream extends BaseStream {

    ChunkStream(String src, int srcSize, int chunkSize) {
        super(src, srcSize, chunkSize);
    }

    @Override
    protected void request(WebView target, @NonNull Chunk[] chunks) {
        if (BuildConfig.DEBUG) {
            Log.e("ChunkStream", "chunk count: " + chunks.length);
        }
        for (Chunk chunk : chunks) {
            JsCallManager.notifyJavaScriptCallNativeFinished(target, chunk.getChunkData());
        }
    }
}
