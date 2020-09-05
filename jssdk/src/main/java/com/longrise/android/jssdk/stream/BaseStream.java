package com.longrise.android.jssdk.stream;

import android.support.annotation.NonNull;
import android.webkit.WebView;

/**
 * Created by godliness on 2020-04-23.
 *
 * @author godliness
 */
abstract class BaseStream {

    private Chunk[] mChunk;

    abstract void request(WebView target, @NonNull Chunk[] chunks);

    final void sendTo(WebView target) {
        request(target, mChunk);
    }

    BaseStream(String src, int srcSize, int chunkSize) {
        segmentationForValue(src, srcSize, chunkSize);
    }

    static final class Chunk {

        private static final String SCHEMA = "chunk://";

        private final int sid;
        private final int count;
        private final String data;

        String getChunkData() {
            return SCHEMA
                    + sid
                    + ":"
                    + count
                    + "/"
                    + data;
        }

        static Chunk create(int sid, int count, String chunk) {
            return new Chunk(sid, count, chunk);
        }

        private Chunk(int sid, int count, String chunk) {
            this.sid = sid;
            this.count = count;
            this.data = chunk;
        }
    }

    private void segmentationForValue(String value, int valueSize, int chunkSize) {
        final int sid = value.hashCode();
        final int chunkCount = valueSize / chunkSize;
        final boolean hasRemainder = valueSize % chunkSize > 0;
        mChunk = new Chunk[hasRemainder ? chunkCount + 1 : chunkCount];
        final int realCount = hasRemainder ? chunkCount + 1 : chunkCount;

        for (int i = 0; i < chunkCount; i++) {
            final int start = i * chunkSize;
            final int end = (i + 1) * chunkSize;
            mChunk[i] = Chunk.create(sid, realCount, value.substring(start, end));
        }
        if (hasRemainder) {
            mChunk[chunkCount] = Chunk.create(sid, realCount, value.substring(chunkCount * chunkSize));
        }
    }
}
