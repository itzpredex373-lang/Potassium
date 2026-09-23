package com.predex.potassium.optimization.world;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.chunks.ChunkOptimizer;
import net.minecraft.client.Minecraft;

/**
 * Conservative world-update gate.
 *
 * This does not cancel vanilla world/entity ticks. It only decides whether
 * Potassium should perform optional client-side maintenance for a chunk.
 */
public final class WorldUpdateOptimizer {
    private WorldUpdateOptimizer() {}

    public static boolean shouldProcessOptionalChunkWork(int chunkX, int chunkZ) {
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeChunkUpdates) {
            return true;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null) {
            return true;
        }

        int playerChunkX = ((int) Math.floor(minecraft.thePlayer.posX)) >> 4;
        int playerChunkZ = ((int) Math.floor(minecraft.thePlayer.posZ)) >> 4;

        int radius = AdaptivePerformanceController.scaleDistance(
                PotassiumConfig.chunkUpdateRadius * 16);
        return ChunkOptimizer.isChunkUseful(
                chunkX, chunkZ, playerChunkX, playerChunkZ,
                Math.max(2, radius / 16));
    }
}
