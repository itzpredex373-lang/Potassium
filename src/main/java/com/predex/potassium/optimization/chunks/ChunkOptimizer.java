package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

/**
 * Cheap chunk-distance decisions used by the Part 2 scheduler.
 *
 * This class does not unload chunks or alter world data. It only decides
 * whether optional client-side chunk work is useful at the current distance.
 */
public final class ChunkOptimizer {
    private ChunkOptimizer() {}

    public static boolean isEnabled() {
        return PotassiumConfig.enabled && PotassiumConfig.optimizeChunkUpdates;
    }

    public static boolean isChunkUseful(Chunk chunk) {
        if (!isEnabled() || chunk == null) {
            return true;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.thePlayer == null) {
            return true;
        }

        int chunkX = chunk.xPosition;
        int chunkZ = chunk.zPosition;
        int playerChunkX = ((int)Math.floor(minecraft.thePlayer.posX)) >> 4;
        int playerChunkZ = ((int)Math.floor(minecraft.thePlayer.posZ)) >> 4;

        return isChunkUseful(chunkX, chunkZ, playerChunkX, playerChunkZ,
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
}
