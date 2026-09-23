package com.predex.potassium.optimization.benchmark;

public final class ChunkTimeTracker {
    private static long start;
    private static double averageMs;

    private ChunkTimeTracker() {}

    public static void begin() { start = System.nanoTime(); }

    public static void end() {
        if (start == 0L) return;
        double ms = (System.nanoTime() - start) / 1_000_000.0D;
        averageMs = averageMs == 0.0D ? ms : (averageMs * 0.85D + ms * 0.15D);
        start = 0L;
    }

    public static double getAverageMs() { return averageMs; }
}