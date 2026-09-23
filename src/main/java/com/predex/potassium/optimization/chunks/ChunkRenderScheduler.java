package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Part 2 chunk scheduler.
 *
 * Keeps the player's chunk state and a nearest-first scan cursor. The cursor
 * is allocation-free and can be consumed by a future RenderGlobal hook.
 */
public final class ChunkRenderScheduler {
    private int lastPlayerChunkX;
    private int lastPlayerChunkZ;
    private boolean initialized;

    private int scanX;
    private int scanZ;
    private int scanRadius;
    private boolean scanReady;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        ChunkUpdateOptimizer.beginTick();

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null || minecraft.thePlayer == null) {
            initialized = false;
            scanReady = false;
            return;
        }

        int chunkX = ((int) Math.floor(minecraft.thePlayer.posX)) >> 4;
        int chunkZ = ((int) Math.floor(minecraft.thePlayer.posZ)) >> 4;

        boolean movedChunk = !initialized
                || chunkX != lastPlayerChunkX
                || chunkZ != lastPlayerChunkZ;

        lastPlayerChunkX = chunkX;
        lastPlayerChunkZ = chunkZ;
        initialized = true;

        if (movedChunk) {
            scanRadius = Math.max(2, PotassiumConfig.chunkUpdateRadius);
            scanX = -scanRadius;
            scanZ = -scanRadius;
            scanReady = true;
        }
    }

    public boolean isPlayerChunk(int chunkX, int chunkZ) {
        return initialized && chunkX == lastPlayerChunkX && chunkZ == lastPlayerChunkZ;
    }

    public boolean shouldSchedule(int chunkX, int chunkZ) {
        if (!ChunkOptimizer.isEnabled() || !initialized) {
            return true;
        }

        return ChunkUpdateOptimizer.shouldProcessChunk(
                chunkX, chunkZ, lastPlayerChunkX, lastPlayerChunkZ);
    }

    /**
     * Returns the next nearby chunk in deterministic nearest-first ring order.
     * The caller still decides whether Minecraft should actually rebuild it.
     */
    public boolean nextCandidate(int[] result) {
        if (!scanReady || result == null || result.length < 2) {
            return false;
        }

        int bestX = 0;
        int bestZ = 0;
        int bestDistance = Integer.MAX_VALUE;
        boolean found = false;

        for (int z = -scanRadius; z <= scanRadius; z++) {
            for (int x = -scanRadius; x <= scanRadius; x++) {
                int distance = x * x + z * z;
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestX = x;
                    bestZ = z;
                    found = true;
                }
            }
        }

        if (!found) {
            scanReady = false;
            return false;
        }

        result[0] = lastPlayerChunkX + bestX;
        result[1] = lastPlayerChunkZ + bestZ;
        scanReady = false;
        return true;
    }

    public int getPlayerChunkX() {
        return lastPlayerChunkX;
    }

    public int getPlayerChunkZ() {
        return lastPlayerChunkZ;
    }
}
