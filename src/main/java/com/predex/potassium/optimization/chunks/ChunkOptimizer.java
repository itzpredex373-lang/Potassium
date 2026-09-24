package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

/**
 * Chunk-distance and priority calculations used by Part 2.
 */
public final class ChunkOptimizer {
    private ChunkOptimizer() {}

    public static boolean isEnabled() {
        return PerformanceManager.isOptimizationEnabled() && PotassiumConfig.optimizeChunkUpdates;
    }

    public static boolean isChunkUseful(Chunk chunk) {
        if (!isEnabled() || chunk == null) {
            return true;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null) {
            return true;
        }

        int playerChunkX = ((int) Math.floor(minecraft.thePlayer.posX)) >> 4;
        int playerChunkZ = ((int) Math.floor(minecraft.thePlayer.posZ)) >> 4;

        return isChunkUseful(
                chunk.xPosition, chunk.zPosition,
                playerChunkX, playerChunkZ,
                PotassiumConfig.chunkUpdateRadius);
    }

    public static boolean isChunkUseful(int chunkX, int chunkZ,
                                        int playerChunkX, int playerChunkZ,
                                        int radius) {
        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        int safeRadius = Math.max(2, radius);

        return dx * dx + dz * dz <= safeRadius * safeRadius;
    }

    /**
     * Squared distance is used so this check does not allocate or call sqrt.
     */
    public static double getDistanceSqToPlayer(int chunkX, int chunkZ) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null) {
            return 0.0D;
        }

        double centerX = chunkX * 16.0D + 8.0D;
        double centerZ = chunkZ * 16.0D + 8.0D;
        double dx = centerX - minecraft.thePlayer.posX;
        double dz = centerZ - minecraft.thePlayer.posZ;

        return dx * dx + dz * dz;
    }

    public static int getPriority(int chunkX, int chunkZ,
                                  int playerChunkX, int playerChunkZ) {
        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        return dx * dx + dz * dz;
    }
}
