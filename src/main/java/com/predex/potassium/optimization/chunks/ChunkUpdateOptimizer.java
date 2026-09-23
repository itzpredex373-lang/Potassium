package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;

/**
 * Per-client-tick budget used by actual chunk-update decisions.
 *
 * Part 4 can dynamically reduce the optional chunk budget when CPU or heap
 * pressure is already high.
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

        if (!CpuOptimizer.shouldRunOptionalWork()) {
            return false;
        }

        int budget = getEffectiveBudget();
        if (updatesThisTick >= budget) {
            return false;
        }

        updatesThisTick++;
        return true;
    }

    public static boolean shouldProcessChunk(int chunkX, int chunkZ,
                                             int playerChunkX, int playerChunkZ) {
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeChunkUpdates) {
            return true;
        }

        if (!ChunkOptimizer.isChunkUseful(
                chunkX, chunkZ, playerChunkX, playerChunkZ,
                PotassiumConfig.chunkUpdateRadius)) {
            return false;
        }

        return tryAcquireUpdateSlot();
    }

    private static int getEffectiveBudget() {
        int budget = PotassiumConfig.maxChunkUpdatesPerTick;

        if (!PotassiumConfig.adaptivePerformance) {
            return budget;
        }

        if (MemoryOptimizer.getPressurePercent() >= 95) {
            return Math.max(1, budget / 2);
        }

        if (MemoryOptimizer.getPressurePercent()
                >= PotassiumConfig.memoryPressureThreshold) {
            return Math.max(1, budget * 3 / 4);
        }

        return budget;
    }

    public static int getUpdatesThisTick() {
        return updatesThisTick;
    }

    public static long getTick() {
        return tick;
    }
}
