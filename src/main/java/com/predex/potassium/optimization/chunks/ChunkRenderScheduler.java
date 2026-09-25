package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class ChunkRenderScheduler {
    private final ChunkWorkQueue workQueue = new ChunkWorkQueue();
    private final ChunkRebuildDeduplicator deduplicator = new ChunkRebuildDeduplicator();

    private int lastPlayerChunkX;
    private int lastPlayerChunkZ;
    private boolean initialized;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        ChunkUpdateOptimizer.beginTick();
        MobileChunkStreaming.beginTick();
        com.predex.potassium.core.PotassiumCoreHooks.beginChunkBuildTick();

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null || minecraft.thePlayer == null) {
            initialized = false;
            workQueue.clear();
            deduplicator.clear();
            return;
        }

        int chunkX = ((int) Math.floor(minecraft.thePlayer.posX)) >> 4;
        int chunkZ = ((int) Math.floor(minecraft.thePlayer.posZ)) >> 4;

        boolean movedChunk = !initialized
                || chunkX != lastPlayerChunkX || chunkZ != lastPlayerChunkZ;

        lastPlayerChunkX = chunkX;
        lastPlayerChunkZ = chunkZ;
        initialized = true;

        if (movedChunk) {
            workQueue.rebuild(chunkX, chunkZ, PotassiumConfig.chunkUpdateRadius,
                    minecraft.thePlayer.motionX, minecraft.thePlayer.motionZ);
            deduplicator.clear();
        }
    }

    public boolean isPlayerChunk(int chunkX, int chunkZ) {
        return initialized && chunkX == lastPlayerChunkX && chunkZ == lastPlayerChunkZ;
    }

    public boolean shouldSchedule(int chunkX, int chunkZ) {
        if (!ChunkOptimizer.isEnabled() || !initialized) return true;

        if (!ChunkUpdateOptimizer.isChunkEligible(
                chunkX, chunkZ, lastPlayerChunkX, lastPlayerChunkZ)) {
            return false;
        }

        return deduplicator.markPending(chunkX, chunkZ);
    }

    public boolean nextScheduledCandidate(int[] result) {
        if (!initialized || result == null || result.length < 2) return false;

        while (workQueue.hasNext()) {
            int x = workQueue.nextChunkX();
            int z = workQueue.nextChunkZ();
            workQueue.advance();

            if (!ChunkUpdateOptimizer.isChunkEligible(
                    x, z, lastPlayerChunkX, lastPlayerChunkZ)) {
                continue;
            }

            if (!deduplicator.markPending(x, z)) continue;

            result[0] = x;
            result[1] = z;
            return true;
        }

        return false;
    }

    public void markChunkComplete(int chunkX, int chunkZ) {
        deduplicator.markComplete(chunkX, chunkZ);
    }

    public int getRemainingCandidates() { return workQueue.remaining(); }
    public int getPlayerChunkX() { return lastPlayerChunkX; }
    public int getPlayerChunkZ() { return lastPlayerChunkZ; }
}
