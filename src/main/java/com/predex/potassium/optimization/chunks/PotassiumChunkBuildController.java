package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Controls the expensive CPU-side RenderChunk rebuild stage.
 *
 * The actual Minecraft 1.8.9 mesh/VBO objects remain vanilla-compatible;
 * this controller replaces the admission/scheduling policy around that
 * pipeline rather than touching GL from worker threads.
 */
public final class PotassiumChunkBuildController {
    private static final Set<RenderChunk> activeBuilds =
            Collections.newSetFromMap(new IdentityHashMap<RenderChunk, Boolean>());

    private static int buildsThisTick;

    private PotassiumChunkBuildController() {}

    public static synchronized void beginTick() {
        buildsThisTick = 0;
        activeBuilds.clear();
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

        int radiusBlocks = AdaptivePerformanceController.scaleDistance(
                Math.max(16, PotassiumConfig.chunkUpdateRadius * 16));

        if (distanceSq > (double) radiusBlocks * radiusBlocks) {
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        int budget = AdaptivePerformanceController.scaleBudget(
                Math.max(1, PotassiumConfig.maxChunkUpdatesPerTick));

        if (buildsThisTick >= budget) {
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        if (!activeBuilds.add(chunk)) {
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        buildsThisTick++;
        return true;
    }

    public static synchronized void finishBuild(RenderChunk chunk) {
        if (chunk != null) activeBuilds.remove(chunk);
    }
}
