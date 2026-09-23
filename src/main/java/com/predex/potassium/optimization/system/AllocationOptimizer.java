package com.predex.potassium.optimization.system;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Reusable primitive-buffer helpers. This avoids creating temporary arrays
 * during repeated Potassium scheduling work.
 */
public final class AllocationOptimizer {
    private AllocationOptimizer() {}

    public static final class IntBufferPool {
        private final Deque<int[]> pool = new ArrayDeque<int[]>();
        private final int max;

        public IntBufferPool(int max) {
            this.max = Math.max(1, max);
        }

        public int[] acquire(int size) {
            int requested = Math.max(1, size);
            int[] value = pool.pollFirst();
            if (value == null || value.length < requested) {
                return new int[requested];
            }
            return value;
        }

        public void release(int[] value) {
            if (value != null && pool.size() < max) {
                pool.offerFirst(value);
            }
        }
    }
}
