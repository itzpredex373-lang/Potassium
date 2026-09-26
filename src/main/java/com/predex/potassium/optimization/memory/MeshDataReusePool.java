package com.predex.potassium.optimization.memory;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Reuses temporary mesh byte arrays without allowing unusually large buffers
 * to remain pinned in the heap indefinitely.
 */
public final class MeshDataReusePool {
    private static final int MAX_RETAINED_BYTES = 4 * 1024 * 1024;

    private final Deque<byte[]> pool = new ArrayDeque<byte[]>();
    private final int capacity;

    public MeshDataReusePool(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    public synchronized byte[] acquire(int minimumSize) {
        int required = Math.max(1, minimumSize);
        while (!pool.isEmpty()) {
            byte[] data = pool.pollFirst();
            if (data.length >= required) return data;
        }
        return new byte[required];
    }

    public synchronized void release(byte[] data) {
        if (data == null || data.length > MAX_RETAINED_BYTES) return;
        if (pool.size() < capacity) pool.offerFirst(data);
    }

    public synchronized void clear() {
        pool.clear();
    }

    public synchronized int size() {
        return pool.size();
    }
}
