package com.predex.potassium.optimization.system;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Part 4/5 memory-pressure controller.
 *
 * Reads JVM heap usage without forcing garbage collection. The value is JVM
 * heap occupancy, not physical device RAM usage.
 */
public final class MemoryOptimizer {
    private static long usedBytes;
    private static long maxBytes;
    private static int pressurePercent;

    private MemoryOptimizer() {}

    public static void update() {
        Runtime runtime = Runtime.getRuntime();

        long max = runtime.maxMemory();
        long used = runtime.totalMemory() - runtime.freeMemory();

        maxBytes = Math.max(1L, max);
        usedBytes = Math.max(0L, used);

        long percent = (usedBytes * 100L) / maxBytes;
        pressurePercent = (int) Math.min(100L, Math.max(0L, percent));
    }

    public static int getPressurePercent() {
        return pressurePercent;
    }

    public static long getUsedBytes() {
        return usedBytes;
    }

    public static long getMaxBytes() {
        return maxBytes;
    }

    public static boolean shouldReduceOptionalWork() {
        return PotassiumConfig.adaptivePerformance
                && pressurePercent >= PotassiumConfig.memoryPressureThreshold;
    }

    public static boolean isUnderPressure() {
        return shouldReduceOptionalWork();
    }

    public static int getParticleBudgetPercent() {
        if (!PotassiumConfig.adaptivePerformance) {
            return 100;
        }

        if (pressurePercent >= 95) {
            return 50;
        }

        if (pressurePercent >= PotassiumConfig.memoryPressureThreshold) {
            return 70;
        }

        return PotassiumConfig.lowMemoryMode ? 85 : 100;
    }

    public static int getChunkBudgetPercent() {
        if (!PotassiumConfig.adaptivePerformance) {
            return 100;
        }

        if (pressurePercent >= 95) {
            return 50;
        }

        if (pressurePercent >= PotassiumConfig.memoryPressureThreshold) {
            return 75;
        }

        return PotassiumConfig.lowMemoryMode ? 90 : 100;
    }
}
