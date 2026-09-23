package com.predex.potassium.optimization.benchmark;

public final class MemoryMonitor {
    private MemoryMonitor() {}

    public static long getUsedBytes() {
        Runtime r = Runtime.getRuntime();
        return r.totalMemory() - r.freeMemory();
    }

    public static long getMaxBytes() {
        return Runtime.getRuntime().maxMemory();
    }

    public static int getUsedPercent() {
        long max = getMaxBytes();
        if (max <= 0L) return 0;
        return (int)Math.min(100L, getUsedBytes() * 100L / max);
    }
}