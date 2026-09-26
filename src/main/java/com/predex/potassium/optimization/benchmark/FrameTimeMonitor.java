package com.predex.potassium.optimization.benchmark;

import java.util.Arrays;

/**
 * Low-overhead frame-time sampler used for stability metrics.
 *
 * The render hot path only records one sample. Low-FPS metrics are refreshed
 * periodically with a linear scan instead of sorting hundreds of samples on
 * the client tick thread.
 */
public final class FrameTimeMonitor {
    private static final int SAMPLE_SIZE = 512;
    private static final int METRIC_REFRESH_INTERVAL = 64;

    private static final double[] samples = new double[SAMPLE_SIZE];
    private static final double[] worstSamples = new double[5];

    private static volatile long lastNanos;
    private static volatile double averageMs;
    private static volatile double varianceMs;
    private static volatile int sampleCount;
    private static volatile int percentileSampleCount;
    private static volatile double cachedOnePercentLow;
    private static volatile double cachedZeroPointOnePercentLow;

    private static int cursor;

    private FrameTimeMonitor() {}

    public static void frame() {
        long now = System.nanoTime();
        long previous = lastNanos;

        if (previous != 0L) {
            double ms = (now - previous) / 1_000_000.0D;
            if (ms < 0.0D || ms > 10000.0D) {
                ms = 0.0D;
            }

            double oldAverage = averageMs;
            double nextAverage = oldAverage == 0.0D
                    ? ms
                    : oldAverage * 0.9D + ms * 0.1D;

            averageMs = nextAverage;
            samples[cursor] = ms;
            cursor = (cursor + 1) % SAMPLE_SIZE;

            int count = sampleCount;
            if (count < SAMPLE_SIZE) {
                count++;
                sampleCount = count;
            }

            double delta = ms - nextAverage;
            double oldVariance = varianceMs;
            varianceMs = oldVariance == 0.0D
                    ? delta * delta
                    : oldVariance * 0.95D + delta * delta * 0.05D;

            if (count >= 32
                    && (count - percentileSampleCount) >= METRIC_REFRESH_INTERVAL) {
                refreshLowFpsMetrics(count);
            }
        }

        lastNanos = now;
        BenchmarkMonitor.recordFrame();
    }

    public static double getAverageMs() {
        return averageMs;
    }

    public static double getLastFrameMs() {
        return sampleCount == 0 ? 0.0D : samples[(cursor - 1 + SAMPLE_SIZE) % SAMPLE_SIZE];
    }

    public static double getVarianceMs() {
        return Math.max(0.0D, varianceMs);
    }

    /**
     * 1% low FPS represented by the 99th-percentile frame time.
     */
    public static double getOnePercentLowFps() {
        return cachedOnePercentLow;
    }

    /**
     * 0.1% low FPS represented by the 99.9th-percentile frame time.
     */
    public static double getZeroPointOnePercentLowFps() {
        return cachedZeroPointOnePercentLow;
    }

    /**
     * Finds the slowest 1% without sorting the full ring buffer.
     * For 512 samples this means tracking only the five slowest frames.
     */
    private static void refreshLowFpsMetrics(int count) {
        int worstCount = Math.max(1, (int) Math.ceil(count * 0.01D));
        double[] worst = worstSamples;
        Arrays.fill(worst, 0.0D);
        int activeWorstCount = Math.min(5, worstCount);

        // Keep the slowest samples in ascending order. This array is tiny;
        // allocation happens only once per metric refresh, not per frame.
        for (int i = 0; i < count; i++) {
            double value = samples[i];
            if (value <= 0.0D) {
                continue;
            }

            int limit = activeWorstCount;
            int insert = limit;

            for (int j = 0; j < limit; j++) {
                if (value > worst[j]) {
                    insert = j;
                    break;
                }
            }

            if (insert < limit) {
                for (int j = limit - 1; j > insert; j--) {
                    worst[j] = worst[j - 1];
                }
                worst[insert] = value;
            }
        }

        double onePercentMs = activeWorstCount == 0 ? 0.0D : worst[activeWorstCount - 1];

        // The single slowest frame is a useful approximation for the 0.1%
        // metric in this short rolling window.
        double worstFrameMs = 0.0D;
        for (int i = 0; i < count; i++) {
            worstFrameMs = Math.max(worstFrameMs, samples[i]);
        }

        cachedOnePercentLow = onePercentMs <= 0.0D
                ? 0.0D
                : 1000.0D / onePercentMs;
        cachedZeroPointOnePercentLow = worstFrameMs <= 0.0D
                ? 0.0D
                : 1000.0D / worstFrameMs;
        percentileSampleCount = count;
    }

    public static int getSampleCount() {
        return sampleCount;
    }

    public static void reset() {
        lastNanos = 0L;
        averageMs = 0.0D;
        varianceMs = 0.0D;
        sampleCount = 0;
        cursor = 0;
        percentileSampleCount = 0;
        cachedOnePercentLow = 0.0D;
        cachedZeroPointOnePercentLow = 0.0D;
        Arrays.fill(samples, 0.0D);
    }
}
