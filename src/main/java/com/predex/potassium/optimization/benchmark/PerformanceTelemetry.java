package com.predex.potassium.optimization.benchmark;

import com.predex.potassium.optimization.adaptive.DynamicQualityController;

import java.util.Arrays;

/**
 * Lightweight frame telemetry with a rolling 120-frame window.
 */
public final class PerformanceTelemetry {
    private static final int WINDOW = 120;
    private static final double[] frameTimes = new double[WINDOW];

    private static long frames, skippedDraws, skippedParticles, skippedChunks;
    private static long frameStartNanos;
    private static double lastFrameMillis;
    private static int frameIndex;
    private static int frameCount;

    private PerformanceTelemetry() {}

    public static void beginFrame() {
        frameStartNanos = System.nanoTime();
    }

    public static void endFrame() {
        if (frameStartNanos == 0L) return;

        lastFrameMillis = (System.nanoTime() - frameStartNanos) / 1000000.0D;
        frames++;

        frameTimes[frameIndex] = lastFrameMillis;
        frameIndex = (frameIndex + 1) % WINDOW;
        frameCount = Math.min(WINDOW, frameCount + 1);

        DynamicQualityController.update(lastFrameMillis);
    }

    public static void skippedDraw() { skippedDraws++; }
    public static void skippedParticle() { skippedParticles++; }
    public static void skippedChunk() { skippedChunks++; }

    public static long getFrames() { return frames; }
    public static long getSkippedDraws() { return skippedDraws; }
    public static long getSkippedParticles() { return skippedParticles; }
    public static long getSkippedChunks() { return skippedChunks; }
    public static double getLastFrameMillis() { return lastFrameMillis; }

    public static double getAverageFrameMillis() {
        if (frameCount == 0) return 0.0D;
        double total = 0.0D;
        for (int i = 0; i < frameCount; i++) total += frameTimes[i];
        return total / frameCount;
    }

    /**
     * Approximate 1% low FPS using the slowest 1% of samples in the rolling window.
     */
    public static double getOnePercentLowFps() {
        if (frameCount == 0) return 0.0D;

        double[] copy = new double[frameCount];
        System.arraycopy(frameTimes, 0, copy, 0, frameCount);
        Arrays.sort(copy);

        int slowIndex = Math.max(0, (int) Math.floor(frameCount * 0.99D));
        double slowMillis = copy[slowIndex];
        if (slowMillis <= 0.0D) return 0.0D;
        return 1000.0D / slowMillis;
    }

    public static void reset() {
        frames = skippedDraws = skippedParticles = skippedChunks = 0L;
        frameStartNanos = 0L;
        lastFrameMillis = 0.0D;
        frameIndex = 0;
        frameCount = 0;
        Arrays.fill(frameTimes, 0.0D);
    }
}
