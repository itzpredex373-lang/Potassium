package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ChunkRenderScheduler {
    private final ChunkWorkQueue workQueue = new ChunkWorkQueue();

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

        if (movedChunk) {
            workQueue.rebuild(chunkX, chunkZ, PotassiumConfig.chunkUpdateRadius);
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

    public boolean nextScheduledCandidate(int[] result) {
        if (!initialized || result == null || result.length < 2) {
            return false;
        }

        while (workQueue.hasNext()) {
            int x = workQueue.nextChunkX();
            int z = workQueue.nextChunkZ();
            workQueue.advance();

            if (ChunkUpdateOptimizer.shouldProcessChunk(
                    x, z, lastPlayerChunkX, lastPlayerChunkZ)) {
                result[0] = x;
                result[1] = z;
                return true;
            }
        }

        return false;
    }

    public int getRemainingCandidates() {
        return workQueue.remaining();
    }

    public int getPlayerChunkX() {
        return lastPlayerChunkX;
    }

    public int getPlayerChunkZ() {
        return lastPlayerChunkZ;
    }
}
