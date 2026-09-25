package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import com.predex.potassium.optimization.world.WorldTransitionOptimizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Controls the expensive CPU-side RenderChunk rebuild stage.
 *
 * The actual Minecraft 1.8.9 mesh/VBO objects remain vanilla-compatible;
 * this controller replaces the admission/scheduling policy around that
 * pipeline rather than touching GL from worker threads.
 */
public final class PotassiumChunkBuildController {
    private static final Map<RenderChunk, Long> activeBuilds =
            Collections.synchronizedMap(new IdentityHashMap<RenderChunk, Long>());

    private static int buildsThisTick;

    private PotassiumChunkBuildController() {}

    public static synchronized void beginTick() {
        buildsThisTick = 0;
        cleanupStaleBuilds(System.nanoTime());
    }

    public static synchronized boolean allowBuild(RenderChunk chunk) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.optimizeChunkUpdates
                || !PotassiumConfig.rendererCoreHooks
                || chunk == null) {
            return true;
        }

        Minecraft mc = Minecraft.getMinecraft();
        Entity camera = mc.getRenderViewEntity();
        if (camera == null) return true;

        BlockPos pos;
        try {
            pos = chunk.getPosition();
        } catch (Throwable ignored) {
            return true;
        }
        if (pos == null) return true;

        double dx = pos.getX() + 8.0D - camera.posX;
        double dy = pos.getY() + 8.0D - camera.posY;
        double dz = pos.getZ() + 8.0D - camera.posZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;

        int radiusBlocks = WorldTransitionOptimizer.scaleChunkDistance(
                Math.max(16, PotassiumConfig.chunkUpdateRadius * 16));

        if (distanceSq > (double) radiusBlocks * radiusBlocks) {
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        int budget = WorldTransitionOptimizer.scaleChunkBudget(
                Math.max(1, PotassiumConfig.maxChunkUpdatesPerTick));

        if (buildsThisTick >= budget) {
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        if (activeBuilds.containsKey(chunk)) {
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        activeBuilds.put(chunk, Long.valueOf(System.nanoTime()));
        buildsThisTick++;
        return true;
    }

    public static synchronized void finishBuild(RenderChunk chunk) {
        if (chunk != null) activeBuilds.remove(chunk);
    }

    private static void cleanupStaleBuilds(long nowNanos) {
        synchronized (activeBuilds) {
            java.util.Iterator<Map.Entry<RenderChunk, Long>> iterator =
                    activeBuilds.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<RenderChunk, Long> entry = iterator.next();
                Long started = entry.getValue();

                if (started == null || nowNanos - started.longValue() > 10_000_000_000L) {
                    iterator.remove();
                }
            }
        }
    }
}
