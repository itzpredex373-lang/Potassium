package com.predex.potassium.optimization.system;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Part 4 CPU/frame-time optimizer.
 *
 * Measures client tick duration and lets optional maintenance back off when
 * the client is already spending too much time in a tick.
 */
public final class CpuOptimizer {
    private static long tickStartNanos;
    private static long lastTickNanos;
    private static long averageTickNanos;

    private CpuOptimizer() {}

    public static void beginTick() {
        tickStartNanos = System.nanoTime();
    }

    public static void endTick() {
        if (tickStartNanos == 0L) {
            return;
        }

        long duration = Math.max(0L, System.nanoTime() - tickStartNanos);
        lastTickNanos = duration;

        if (averageTickNanos == 0L) {
            averageTickNanos = duration;
        } else {
            averageTickNanos = (averageTickNanos * 7L + duration) / 8L;
        }
    }

    public static double getLastTickMillis() {
        return lastTickNanos / 1_000_000.0D;
    }

    public static double getAverageTickMillis() {
        return averageTickNanos / 1_000_000.0D;
    }

    public static boolean shouldRunOptionalWork() {
        if (!PotassiumConfig.adaptivePerformance) {
            return true;
        }

        long budgetNanos = PotassiumConfig.cpuBudgetMillis * 1_000_000L;
        return averageTickNanos == 0L || averageTickNanos <= budgetNanos;
    }
}
