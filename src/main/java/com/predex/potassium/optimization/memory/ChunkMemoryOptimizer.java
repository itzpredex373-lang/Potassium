package com.predex.potassium.optimization.memory;

public final class ChunkMemoryOptimizer {
    private ChunkMemoryOptimizer() {}

    public static int estimateBytes(int blockCount, int bytesPerBlock) {
        if (blockCount <= 0 || bytesPerBlock <= 0) return 0;
        long value = (long) blockCount * bytesPerBlock;
        return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) value;
    }

    public static boolean isReasonableAllocation(int bytes, int limitMb) {
        return bytes >= 0 && bytes <= Math.max(1, limitMb) * 1024 * 1024;
    }
}