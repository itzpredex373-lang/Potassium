package com.predex.potassium.optimization.rendering;

import java.util.ArrayDeque;
import java.util.Deque;

public final class VertexBufferOptimizer {
    private static final int MAX_BUFFERS = 8;
    private static final Deque<byte[]> pool = new ArrayDeque<byte[]>();
    private VertexBufferOptimizer() {}

    public static synchronized byte[] acquire(int minimumSize) {
        int size = Math.max(256, minimumSize);
        byte[] best = null;
        for (byte[] candidate : pool) {
            if (candidate.length >= size && (best == null || candidate.length < best.length)) best = candidate;
        }
        if (best != null) { pool.remove(best); return best; }
        return new byte[size];
    }

    public static synchronized void release(byte[] buffer) {
        if (buffer != null && pool.size() < MAX_BUFFERS) pool.addLast(buffer);
    }

    public static synchronized void clear() { pool.clear(); }
}
