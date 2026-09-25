package com.predex.potassium.optimization.benchmark;

/**
 * Frame-time sampler used for stability metrics.
 *
 * The ring buffer is allocation-free during normal rendering. Percentiles use
 * a reusable scratch buffer only when the cached low-FPS metrics are refreshed.
 */
public final class FrameTimeMonitor {
    private static final int SAMPLE_SIZE = 512;
    private static final double[] samples = new double[SAMPLE_SIZE];
    private static final double[] percentileScratch = new double[SAMPLE_SIZE];

    private static long lastNanos;
    private static double averageMs;
    private static double varianceMs;
    private static int sampleCount;
    private static int cursor;
    private static int percentileSampleCount;
    private static double cachedOnePercentLow;
    private static double cachedZeroPointOnePercentLow;

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
            if (sampleCount < SAMPLE_SIZE) {
                sampleCount++;
            }

            double delta = ms - averageMs;
            varianceMs = varianceMs == 0.0D
                    ? delta * delta
                    : (varianceMs * 0.95D + delta * delta * 0.05D);
        }

        lastNanos = now;
        BenchmarkMonitor.recordFrame();
    }

    public static synchronized double getAverageMs() {
        return averageMs;
    }

    public static synchronized double getVarianceMs() {
        return Math.max(0.0D, varianceMs);
    }

    /**
     * 1% low FPS: FPS corresponding to the 99th percentile frame time.
     */
    public static synchronized double getOnePercentLowFps() {
        refreshPercentilesIfNeeded();
        return cachedOnePercentLow;
    }

    /**
     * 0.1% low FPS: FPS corresponding to the 99.9th percentile frame time.
     */
    public static synchronized double getZeroPointOnePercentLowFps() {
        refreshPercentilesIfNeeded();
        return cachedZeroPointOnePercentLow;
    }

    private static void refreshPercentilesIfNeeded() {
        if (sampleCount < 2) {
            return;
        }
        if (sampleCount - percentileSampleCount < 8 && percentileSampleCount != 0) {
            return;
        }

        int count = sampleCount;
        if (count < SAMPLE_SIZE) {
            System.arraycopy(samples, 0, percentileScratch, 0, count);
        } else {
            // The ring is full. Reconstruct chronological order without
            // allocating a temporary array.
            int tail = cursor;
            int first = SAMPLE_SIZE - tail;
            System.arraycopy(samples, tail, percentileScratch, 0, first);
            System.arraycopy(samples, 0, percentileScratch, first, tail);
        }

        insertionSort(percentileScratch, count);
        cachedOnePercentLow = percentileFps(0.99D, count);
        cachedZeroPointOnePercentLow = percentileFps(0.999D, count);
        percentileSampleCount = sampleCount;
    }

    private static void insertionSort(double[] values, int count) {
        for (int i = 1; i < count; i++) {
            double value = values[i];
            int j = i - 1;
            while (j >= 0 && values[j] > value) {
                values[j + 1] = values[j];
                j--;
            }
            values[j + 1] = value;
        }
    }

    private static double percentileFps(double percentile, int count) {
        if (count < 2) {
            return 0.0D;
        }

        int index = (int) Math.ceil((count - 1) * percentile);
        index = Math.max(0, Math.min(count - 1, index));
        double ms = percentileScratch[index];
        return ms <= 0.0D ? 0.0D : 1000.0D / ms;
    }

    public static synchronized int getSampleCount() {
        return sampleCount;
    }

    public static synchronized void reset() {
        lastNanos = 0L;
        averageMs = 0.0D;
        varianceMs = 0.0D;
        sampleCount = 0;
        cursor = 0;
        percentileSampleCount = 0;
        cachedOnePercentLow = 0.0D;
        cachedZeroPointOnePercentLow = 0.0D;
        java.util.Arrays.fill(samples, 0.0D);
        java.util.Arrays.fill(percentileScratch, 0.0D);
    }
}
