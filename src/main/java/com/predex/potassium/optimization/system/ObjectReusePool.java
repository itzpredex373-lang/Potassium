package com.predex.potassium.optimization.system;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public final class ObjectReusePool<T> {
    private final Deque<T> pool = new ArrayDeque<T>();
    private final int capacity;

    public ObjectReusePool(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    public T acquire(Supplier<T> factory) {
        T value = pool.pollFirst();
        if (value != null) {
            return value;
        }
        if (factory == null) {
            return null;
        }
        return factory.get();
    }

    public void release(T value) {
        if (value != null && pool.size() < capacity) {
            pool.offerFirst(value);
        }
    }

    public int size() {
        return pool.size();
    }
}
