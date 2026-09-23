package com.predex.potassium.optimization.particles;

import java.util.ArrayDeque;
import java.util.Deque;

/** Small bounded object pool for Potassium-owned particle helper objects. */
public final class ParticlePool<T> {
    private final Deque<T> pool = new ArrayDeque<T>();
    private final int capacity;

    public ParticlePool(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    public T acquire() {
        return pool.pollFirst();
    }

    public void release(T value) {
        if (value != null && pool.size() < capacity) pool.offerFirst(value);
    }

    public void clear() {
        pool.clear();
    }

    public int size() {
        return pool.size();
    }
}