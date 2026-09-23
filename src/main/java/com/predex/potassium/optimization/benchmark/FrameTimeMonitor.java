package com.predex.potassium.optimization.benchmark;

public final class FrameTimeMonitor {
    private static long lastNanos;
    private static double averageMs;

    private FrameTimeMonitor() {}

    public static void frame() {
        long now = System.nanoTime();
        if (lastNanos != 0L) {
            double ms = (now - lastNanos) / 1_000_000.0D;
            averageMs = averageMs == 0.0D ? ms : (averageMs * 0.9D + ms * 0.1D);
        }
        lastNanos = now;
        BenchmarkMonitor.recordFrame();
    }

    public static double getAverageMs() { return averageMs; }

    public static void reset() {
        lastNanos = 0L;
        averageMs = 0.0D;
    }
}