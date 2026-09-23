package com.predex.potassium.optimization.memory;

public final class AllocationTracker {
    private static long estimatedBytes;
    private static long allocations;

    private AllocationTracker() {}

    public static void record(long bytes) {
        if (bytes <= 0) return;
        estimatedBytes += bytes;
        allocations++;
    }

    public static long getEstimatedBytes() { return estimatedBytes; }
    public static long getAllocations() { return allocations; }

    public static void reset() {
        estimatedBytes = 0L;
        allocations = 0L;
    }
}