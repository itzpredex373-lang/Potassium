package com.predex.potassium.optimization.benchmark;

import com.predex.potassium.optimization.system.CpuOptimizer;

public final class BenchmarkMonitor {
    private static long frames;
    private static long startedNanos;
    private static double frameTimeMs;

    private BenchmarkMonitor() {}

    public static void begin() {
        if (startedNanos == 0L) startedNanos = System.nanoTime();
    }

    public static void recordFrame() {
        if (startedNanos == 0L) begin();
        frames++;
        frameTimeMs = CpuOptimizer.getAverageTickMillis();
    }

    public static long getFrames() { return frames; }
    public static double getAverageFrameTimeMs() { return frameTimeMs; }

    public static double getMeasuredFps() {
        if (startedNanos == 0L) return 0.0D;
        long elapsed = System.nanoTime() - startedNanos;
        return elapsed <= 0L ? 0.0D : frames * 1_000_000_000.0D / elapsed;
    }

    public static void reset() {
        frames = 0L;
        startedNanos = 0L;
        frameTimeMs = 0.0D;
    }
}