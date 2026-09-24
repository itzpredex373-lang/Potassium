package com.predex.potassium.optimization.benchmark;

import com.predex.potassium.optimization.adaptive.DynamicQualityController;

public final class PerformanceTelemetry {
    private static long frames, skippedDraws, skippedParticles, skippedChunks;
    private static long frameStartNanos;
    private static double lastFrameMillis;

    private PerformanceTelemetry() {}
    public static void beginFrame() { frameStartNanos = System.nanoTime(); }
    public static void endFrame() {
        if (frameStartNanos == 0L) return;
        lastFrameMillis = (System.nanoTime() - frameStartNanos) / 1000000.0D;
        frames++;
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
    public static void reset() {
        frames = skippedDraws = skippedParticles = skippedChunks = 0L;
        frameStartNanos = 0L;
        lastFrameMillis = 0.0D;
    }
}
