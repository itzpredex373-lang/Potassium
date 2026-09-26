package com.predex.potassium.optimization.benchmark;

public final class BenchmarkMonitor {
    private static long frames;
    private static long startedNanos;
    private static double frameTimeMs;\n    private static double minimumFrameTimeMs;\n    private static double maximumFrameTimeMs;

    private BenchmarkMonitor() {}

    public static void begin() {
        if (startedNanos == 0L) startedNanos = System.nanoTime();
    }

    public static void recordFrame() {
        if (startedNanos == 0L) begin();
        frames++;
        frameTimeMs = FrameTimeMonitor.getAverageMs();\n        double current = FrameTimeMonitor.getAverageMs();\n        if (minimumFrameTimeMs == 0.0D || current < minimumFrameTimeMs) minimumFrameTimeMs = current;\n        if (current > maximumFrameTimeMs) maximumFrameTimeMs = current;
    }

    public static long getFrames() { return frames; }
    public static double getAverageFrameTimeMs() { return frameTimeMs; }

    public static double getMinimumFrameTimeMs() { return minimumFrameTimeMs; }
    public static double getMaximumFrameTimeMs() { return maximumFrameTimeMs; }

    public static double getMeasuredFps() {
        if (startedNanos == 0L) return 0.0D;
        long elapsed = System.nanoTime() - startedNanos;
        return elapsed <= 0L ? 0.0D : frames * 1_000_000_000.0D / elapsed;
    }

    public static void reset() {
        frames = 0L;
        startedNanos = 0L;
        frameTimeMs = 0.0D;\n        minimumFrameTimeMs = 0.0D;\n        maximumFrameTimeMs = 0.0D;
    }
}