package com.predex.potassium.optimization.memory;

import java.util.ArrayDeque;
import java.util.Deque;

public final class MeshDataReusePool {
    private final Deque<byte[]> pool = new ArrayDeque<byte[]>();
    private final int capacity;

    public MeshDataReusePool(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    public byte[] acquire(int minimumSize) {
        byte[] data = pool.pollFirst();
        if (data == null || data.length < minimumSize) return new byte[Math.max(1, minimumSize)];
        return data;
    }

    public void release(byte[] data) {
        if (data != null && pool.size() < capacity) pool.offerFirst(data);
    }

    public void clear() { pool.clear(); }
    public int size() { return pool.size(); }
}