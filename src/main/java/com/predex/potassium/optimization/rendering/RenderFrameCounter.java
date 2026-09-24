package com.predex.potassium.optimization.rendering;

/** Monotonic render-frame id shared by visibility caches. */
public final class RenderFrameCounter {
    private static long frameId;

    private RenderFrameCounter() {}

    public static void beginFrame() {
        frameId++;
    }

    public static long getFrameId() {
        return frameId;
    }
}
