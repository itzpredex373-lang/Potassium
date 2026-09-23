package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Lightweight per-tick budget for optional chunk maintenance.
 *
 * The budget prevents a future chunk hook from processing an unbounded number
 * of chunks in one client tick. No world data is modified here.
 */
public final class ChunkUpdateOptimizer {
    private static long tick;
    private static int updatesThisTick;

    private ChunkUpdateOptimizer() {}

    public static void beginTick() {
        tick++;
        updatesThisTick = 0;
    }

    public static boolean tryAcquireUpdateSlot() {
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeChunkUpdates) {
            return true;
        }

        if (updatesThisTick >= PotassiumConfig.maxChunkUpdatesPerTick) {
            return false;
        }

        updatesThisTick++;
        return true;
    }

    public static int getUpdatesThisTick() {
        return updatesThisTick;
    }

    public static long getTick() {
        return tick;
    }
}
