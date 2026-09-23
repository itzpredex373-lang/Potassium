package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Part 2 chunk scheduler.
 *
 * Maintains an allocation-free spiral candidate cursor around the player.
 * This provides deterministic near-to-far chunk planning without allocating
 * lists every tick. A future RenderGlobal hook can consume nextCandidate().
 */
public final class ChunkRenderScheduler {
    private int lastPlayerChunkX;
    private int lastPlayerChunkZ;
    private boolean initialized;

    private int ringRadius;
    private int ringX;
    private int ringZ;
    private int ringSide;
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
            resetScan();
        }
    }

    private void resetScan() {
        ringRadius = 0;
        ringX = 0;
        ringZ = 0;
        ringSide = 0;
        scanReady = true;
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
     * Returns one nearby chunk per call, from the center outward.
     * The supplied array must have length >= 2.
     */
    public boolean nextCandidate(int[] result) {
        if (!scanReady || result == null || result.length < 2) {
            return false;
        }

        int x = ringX;
        int z = ringZ;
        int radius = Math.max(0, PotassiumConfig.chunkUpdateRadius);

        result[0] = lastPlayerChunkX + x;
        result[1] = lastPlayerChunkZ + z;

        if (ringRadius == 0) {
            ringRadius = 1;
            ringX = -1;
            ringZ = -1;
            ringSide = 0;
            return true;
        }

        if (ringSide == 0) {
            ringX++;
            if (ringX > ringRadius) {
                ringSide = 1;
                ringX = ringRadius;
                ringZ++;
            }
        } else if (ringSide == 1) {
            ringZ++;
            if (ringZ > ringRadius) {
                ringSide = 2;
                ringZ = ringRadius;
                ringX--;
            }
        } else if (ringSide == 2) {
            ringX--;
            if (ringX < -ringRadius) {
                ringSide = 3;
                ringX = -ringRadius;
                ringZ--;
            }
        } else {
            ringZ--;
            if (ringZ < -ringRadius) {
                ringRadius++;
                if (ringRadius > radius) {
                    scanReady = false;
                } else {
                    ringX = -ringRadius;
                    ringZ = -ringRadius;
                    ringSide = 0;
                }
            }
        }

        return true;
    }

    public int getPlayerChunkX() {
        return lastPlayerChunkX;
    }

    public int getPlayerChunkZ() {
        return lastPlayerChunkZ;
    }
}
