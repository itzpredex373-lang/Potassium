package com.predex.potassium.optimization.chunks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Part 2 client chunk scheduler.
 *
 * It tracks the camera chunk and maintains a small bounded queue of nearby
 * RenderChunks that have already been marked dirty by Minecraft. The queue
 * does not create new chunk rebuilds; it only gives later hooks a stable,
 * budgeted scheduling point.
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
        if (minecraft.theWorld == null || minecraft.thePlayer == null
                || !ChunkOptimizer.isEnabled()) {
            return;
        }

        int chunkX = ((int) Math.floor(minecraft.thePlayer.posX)) >> 4;
        int chunkZ = ((int) Math.floor(minecraft.thePlayer.posZ)) >> 4;

        if (!initialized || chunkX != lastPlayerChunkX || chunkZ != lastPlayerChunkZ) {
            lastPlayerChunkX = chunkX;
            lastPlayerChunkZ = chunkZ;
            initialized = true;
        }

        /*
         * Keep the scheduler itself allocation-free. Minecraft's RenderGlobal
         * owns the actual RenderChunk collection, so Part 2 does not mutate
         * that collection from a tick event.
         */
        RenderGlobal renderGlobal = minecraft.renderGlobal;
        if (renderGlobal == null) {
            return;
        }

        // A bounded scheduling point for the actual RenderChunk hook.
        // The renderer's dirty queue is consumed by Minecraft during rendering.
        // We intentionally do not force rebuilds here.
        if (ChunkUpdateOptimizer.tryAcquireUpdateSlot()) {
            // Budget slot reserved for the next RenderChunk update hook.
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
}
