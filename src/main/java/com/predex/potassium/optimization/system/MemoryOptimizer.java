package com.predex.potassium.optimization.system;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Part 4 memory optimization.
 *
 * Monitors JVM heap pressure without forcing garbage collection. Under high
 * pressure, optional particle work receives a smaller budget.
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
        pressurePercent = (int) Math.min(100L, (usedBytes * 100L) / maxBytes);
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
        return PotassiumConfig.adaptivePerformance
                && pressurePercent >= PotassiumConfig.memoryPressureThreshold;
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

        return 100;
    }
}
