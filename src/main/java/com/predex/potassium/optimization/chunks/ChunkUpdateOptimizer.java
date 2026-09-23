package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;

/**
 * Per-client-tick budget for optional chunk work.
 *
 * The budget is deliberately a gate rather than a forced rebuild. A future
 * RenderGlobal/RenderChunk hook can call shouldProcessChunk() before work.
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
        int configured = Math.max(1, PotassiumConfig.maxChunkUpdatesPerTick);

        if (!PotassiumConfig.adaptivePerformance) {
            return configured;
        }

        int percent = MemoryOptimizer.getChunkBudgetPercent();
        return Math.max(1, configured * percent / 100);
    }

    public static int getUpdatesThisTick() {
        return updatesThisTick;
    }

    public static int getEffectiveBudgetForDiagnostics() {
        return getEffectiveBudget();
    }

    public static long getTick() {
        return tick;
    }
}
