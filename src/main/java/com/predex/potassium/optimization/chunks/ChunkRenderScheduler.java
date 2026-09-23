package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Part 2 chunk scheduler.
 *
 * Maintains a nearest-first, allocation-free work plan around the player.
 * This is a real scheduling layer, but it deliberately does not inject into
 * RenderGlobal/RenderChunk yet; that deeper hook requires a version-specific
 * coremod/ASM layer and must be tested separately.
 */
public final class ChunkRenderScheduler {
    private final ChunkWorkQueue workQueue = new ChunkWorkQueue();

    private int lastPlayerChunkX;
    private int lastPlayerChunkZ;
    private boolean initialized;
    private long lastQueueTick;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        ChunkUpdateOptimizer.beginTick();

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null || minecraft.thePlayer == null) {
            initialized = false;
            workQueue.clear();
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

        if (movedChunk || lastQueueTick == 0L
                || ChunkUpdateOptimizer.getTick() - lastQueueTick >= 8L) {
            workQueue.rebuild(chunkX, chunkZ, PotassiumConfig.chunkUpdateRadius);
            lastQueueTick = ChunkUpdateOptimizer.getTick();
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

    public boolean isPlannedChunk(int chunkX, int chunkZ) {
        for (int i = 0; i < workQueue.size(); i++) {
            if (workQueue.getChunkX(i) == chunkX
                    && workQueue.getChunkZ(i) == chunkZ) {
                return true;
            }
        }
        return false;
    }

    public int getPlannedChunkCount() {
        return workQueue.size();
    }

    public int getPlannedChunkX(int index) {
        return workQueue.getChunkX(index);
    }

    public int getPlannedChunkZ(int index) {
        return workQueue.getChunkZ(index);
    }

    public int getPlayerChunkX() {
        return lastPlayerChunkX;
    }

    public int getPlayerChunkZ() {
        return lastPlayerChunkZ;
    }
}
