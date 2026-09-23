package com.predex.potassium.optimization.world;

import java.util.ArrayDeque;
import java.util.Queue;

public final class LightUpdateScheduler {
    private final Queue<Long> queue = new ArrayDeque<Long>();

    public boolean enqueue(int x, int y, int z) {
        if (queue.size() >= 1024) return false;
        queue.offer(pack(x, y, z));
        return true;
    }

    public long poll() {
        Long value = queue.poll();
        return value == null ? Long.MIN_VALUE : value.longValue();
    }

    public int size() { return queue.size(); }

    private static long pack(int x, int y, int z) {
        return ((long)(x & 0x1fffff) << 43) | ((long)(y & 0x1ff) << 34)
                | (z & 0x3ffffffffL);
    }
}