package com.predex.potassium.optimization.chunks;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Part 2 client chunk scheduler.
 *
 * Tracks the player's current chunk and exposes bounded decisions for actual
 * chunk-update hooks. It does not force rebuilds or mutate Minecraft internals.
 */
public final class ChunkRenderScheduler {
    private int lastPlayerChunkX;
    private int lastPlayerChunkZ;
    private boolean initialized;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        ChunkUpdateOptimizer.beginTick();

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null || minecraft.thePlayer == null) {
            return;
        }

        int chunkX = ((int) Math.floor(minecraft.thePlayer.posX)) >> 4;
        int chunkZ = ((int) Math.floor(minecraft.thePlayer.posZ)) >> 4;

        lastPlayerChunkX = chunkX;
        lastPlayerChunkZ = chunkZ;
        initialized = true;
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

    public int getPlayerChunkX() {
        return lastPlayerChunkX;
    }

    public int getPlayerChunkZ() {
        return lastPlayerChunkZ;
    }
}
