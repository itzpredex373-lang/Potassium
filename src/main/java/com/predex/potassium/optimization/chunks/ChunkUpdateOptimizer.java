package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Per-client-tick budget used by actual chunk-update decisions.
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

    public static int getUpdatesThisTick() {
        return updatesThisTick;
    }

    public static long getTick() {
        return tick;
    }
}
