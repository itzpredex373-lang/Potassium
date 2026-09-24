package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Performance techniques inspired by OptiFine's documented performance options.
 *
 * This class deliberately does not copy OptiFine code. It provides Potassium-owned
 * decisions that can be used by Potassium's renderer/scheduler hooks.
 */
public final class OptiFinePerformanceParity {
    private static final int TRIG_SIZE = 65536;
    private static final float[] SIN = new float[TRIG_SIZE];

    static {
        for (int i = 0; i < TRIG_SIZE; i++) {
            SIN[i] = (float) Math.sin((i * Math.PI * 2.0D) / TRIG_SIZE);
        }
    }

    private OptiFinePerformanceParity() {}

    public static float fastSin(float radians) {
        if (!PotassiumConfig.fastMath) return (float) Math.sin(radians);
        return SIN[((int) (radians * 10430.378F)) & 65535];
    }

    public static float fastCos(float radians) {
        if (!PotassiumConfig.fastMath) return (float) Math.cos(radians);
        return SIN[((int) (radians * 10430.378F + 16384.0F)) & 65535];
    }

    public static boolean allowFastRender() {
        return PotassiumConfig.enabled && PotassiumConfig.fastRender;
    }

    public static boolean allowSmartAnimation(boolean visible) {
        if (!PotassiumConfig.enabled || !PotassiumConfig.smartAnimations) return true;
        return visible;
    }

    public static int regionKey(int chunkX, int chunkZ) {
        int rx = chunkX >> 4;
        int rz = chunkZ >> 4;
        return (rx * 7340033) ^ (rz * 1610612741);
    }

    public static int getChunkBudget(int configured, boolean playerStandingStill, boolean localWorld) {
        int budget = Math.max(1, configured);

        if (PotassiumConfig.dynamicChunkUpdates && playerStandingStill) {
            budget = Math.min(16, budget + 1);
        }

        if (PotassiumConfig.lazyChunkLoading && localWorld) {
            budget = Math.max(1, budget);
        }

        return budget;
    }

    public static boolean shouldDistributeWorldWork(boolean localWorld) {
        return PotassiumConfig.enabled && PotassiumConfig.smoothWorld && localWorld;
    }

    public static boolean smoothFpsEnabled() {
        return PotassiumConfig.enabled && PotassiumConfig.smoothFps;
    }

    public static boolean renderRegionsEnabled() {
        return PotassiumConfig.enabled && PotassiumConfig.renderRegions;
    }
}
