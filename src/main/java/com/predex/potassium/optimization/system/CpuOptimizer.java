package com.predex.potassium.optimization.system;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Part 4 CPU/frame-time optimizer.
 *
 * Uses a small hysteresis window so one slow client tick does not disable
 * optional work, while sustained CPU pressure quickly backs it off.
 */
public final class CpuOptimizer {
    private static long tickStartNanos;
    private static long lastTickNanos;
    private static long averageTickNanos;
    private static int pressureTicks;
    private static int recoveryTicks;

    private CpuOptimizer() {}

    public static void beginTick() {
        tickStartNanos = System.nanoTime();
    }

    public static void endTick() {
        if (tickStartNanos == 0L) return;

        long duration = Math.max(0L, System.nanoTime() - tickStartNanos);
        lastTickNanos = duration;

        if (averageTickNanos == 0L) {
            averageTickNanos = duration;
        } else {
            averageTickNanos = (averageTickNanos * 7L + duration) / 8L;
        }

        long budgetNanos = getBudgetNanos();
        if (duration > budgetNanos) {
            pressureTicks++;
            recoveryTicks = 0;
        } else if (duration < (budgetNanos * 85L) / 100L) {
            recoveryTicks++;
            pressureTicks = 0;
        } else {
            pressureTicks = Math.max(0, pressureTicks - 1);
            recoveryTicks = Math.max(0, recoveryTicks - 1);
        }

        if (pressureTicks > 60) pressureTicks = 60;
        if (recoveryTicks > 60) recoveryTicks = 60;
    }

    private static long getBudgetNanos() {
        long millis = Math.max(1L, Math.min(200L, (long) PotassiumConfig.cpuBudgetMillis));
        return millis * 1_000_000L;
    }

    public static double getLastTickMillis() {
        return lastTickNanos / 1_000_000.0D;
    }

    public static double getAverageTickMillis() {
        return averageTickNanos / 1_000_000.0D;
    }

    public static boolean shouldRunOptionalWork() {
        if (!PotassiumConfig.adaptivePerformance) return true;

        if (pressureTicks >= 3) return false;
        if (recoveryTicks >= 2) return true;

        return averageTickNanos == 0L || averageTickNanos <= getBudgetNanos();
    }
}
