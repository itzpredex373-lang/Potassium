package com.predex.potassium.optimization.rendering;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * CPU-side staging buffer pool.
 *
 * This class does not own OpenGL VertexBuffer objects. It only reuses temporary
 * byte arrays used by Potassium's renderer helpers, keeping allocations bounded
 * and avoiding a single huge retained array after a transient rebuild spike.
 */
public final class VertexBufferOptimizer {
    private static final int MAX_BUFFERS = 16;
    private static final int MAX_RETAINED_BYTES = 4 * 1024 * 1024;
    private static final int MAX_SINGLE_BUFFER = 1024 * 1024;

    private static final Deque<byte[]> pool = new ArrayDeque<byte[]>();
    private static int retainedBytes;

    private VertexBufferOptimizer() {}

    public static synchronized byte[] acquire(int minimumSize) {
        int size = Math.max(256, minimumSize);
        byte[] best = null;

        for (byte[] candidate : pool) {
            if (candidate.length >= size
                    && candidate.length <= MAX_SINGLE_BUFFER
                    && (best == null || candidate.length < best.length)) {
                best = candidate;
            }
        }

        if (best != null) {
            pool.remove(best);
            retainedBytes -= best.length;
            return best;
        }

        return new byte[Math.min(size, MAX_SINGLE_BUFFER)];
    }

    public static synchronized void release(byte[] buffer) {
        if (buffer == null
                || buffer.length < 256
                || buffer.length > MAX_SINGLE_BUFFER
                || pool.size() >= MAX_BUFFERS
                || retainedBytes + buffer.length > MAX_RETAINED_BYTES) {
            return;
        }

        pool.addLast(buffer);
        retainedBytes += buffer.length;
    }

    public static synchronized void clear() {
        pool.clear();
        retainedBytes = 0;
    }

    public static synchronized int pooledBytes() {
        return retainedBytes;
    }

    public static synchronized int pooledBuffers() {
        return pool.size();
    }
}
