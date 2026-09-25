package com.predex.potassium.optimization.benchmark;

import java.util.Arrays;

/**
 * Frame-time sampler used for stability metrics.
 *
 * The ring buffer is intentionally small and allocation-free during normal
 * rendering. Percentile calculations are performed only when queried.
 */
public final class FrameTimeMonitor {
    private static final int SAMPLE_SIZE = 512;
    private static final double[] samples = new double[SAMPLE_SIZE];

    private static long lastNanos;
    private static double averageMs;
    private static double varianceMs;
    private static int sampleCount;
    private static int cursor;

    private FrameTimeMonitor() {}

    public static synchronized void frame() {
        long now = System.nanoTime();
        if (lastNanos != 0L) {
            double ms = (now - lastNanos) / 1_000_000.0D;
            if (ms < 0.0D || ms > 10000.0D) {
                ms = 0.0D;
            }

            averageMs = averageMs == 0.0D ? ms : (averageMs * 0.9D + ms * 0.1D);
            samples[cursor] = ms;
            cursor = (cursor + 1) % SAMPLE_SIZE;
            if (sampleCount < SAMPLE_SIZE) sampleCount++;

            double delta = ms - averageMs;
            varianceMs = varianceMs == 0.0D
                    ? delta * delta
                    : (varianceMs * 0.95D + delta * delta * 0.05D);
        }

        lastNanos = now;
        BenchmarkMonitor.recordFrame();
    }

    public static synchronized double getAverageMs() { return averageMs; }

    public static synchronized double getVarianceMs() {
        return Math.max(0.0D, varianceMs);
    }

    /**
     * 1% low FPS: the FPS corresponding to the 99th percentile frame time.
     */
    public static synchronized double getOnePercentLowFps() {
        return percentileFps(0.99D);
    }

    /**
     * 0.1% low FPS: the FPS corresponding to the 99.9th percentile frame time.
     */
    public static synchronized double getZeroPointOnePercentLowFps() {
        return percentileFps(0.999D);
    }

    private static double percentileFps(double percentile) {
        if (sampleCount < 2) return 0.0D;

        double[] copy = new double[sampleCount];
        System.arraycopy(samples, 0, copy, 0, sampleCount);
        Arrays.sort(copy);

        int index = (int) Math.ceil((sampleCount - 1) * percentile);
        index = Math.max(0, Math.min(sampleCount - 1, index));
        double ms = copy[index];
        return ms <= 0.0D ? 0.0D : 1000.0D / ms;
    }

    public static synchronized int getSampleCount() { return sampleCount; }

    public static synchronized void reset() {
        lastNanos = 0L;
        averageMs = 0.0D;
        varianceMs = 0.0D;
        sampleCount = 0;
        cursor = 0;
        Arrays.fill(samples, 0.0D);
    }
}
