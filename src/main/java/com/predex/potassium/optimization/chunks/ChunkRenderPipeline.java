package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Deep chunk-render admission layer.
 *
 * Vanilla 1.8.9 still owns the actual GL upload/build implementation, but
 * Potassium controls which rebuild jobs are allowed to enter that pipeline.
 * This keeps the client-thread/worker-thread contract intact while reducing
 * redundant and low-value rebuild dispatches.
 */
public final class ChunkRenderPipeline {
    private static final Set<RenderChunk> dispatched =
            Collections.newSetFromMap(new IdentityHashMap<RenderChunk, Boolean>());

    private static long deadlineNanos;
    private static int dispatchedThisWindow;
    private static int rejectedThisWindow;

    private ChunkRenderPipeline() {}

    public static synchronized void beginUpdateWindow(long finishTimeNano) {
        deadlineNanos = finishTimeNano;
        dispatchedThisWindow = 0;
        rejectedThisWindow = 0;
        dispatched.clear();
    }

    public static synchronized boolean allowChunkDispatch(
            ChunkRenderDispatcher dispatcher, RenderChunk renderChunk) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.rendererCoreHooks
                || !PotassiumConfig.optimizeChunkUpdates
                || renderChunk == null) {
            return true;
        }

        if (dispatcher == null) return true;

        Minecraft mc = Minecraft.getMinecraft();
        Entity camera = mc.getRenderViewEntity();
        if (camera == null) return true;

        if (deadlineNanos > 0L && System.nanoTime() >= deadlineNanos) {
            rejectedThisWindow++;
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        if (dispatched.contains(renderChunk)) {
            rejectedThisWindow++;
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        BlockPos position = renderChunk.getPosition();
        if (position == null) return true;

        double dx = position.getX() + 8.0D - camera.posX;
        double dy = position.getY() + 8.0D - camera.posY;
        double dz = position.getZ() + 8.0D - camera.posZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;

        int radius = AdaptivePerformanceController.scaleDistance(
                Math.max(16, PotassiumConfig.chunkUpdateRadius * 16));

        if (distanceSq > (double) radius * (double) radius) {
            rejectedThisWindow++;
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        int configuredBudget = Math.max(1, PotassiumConfig.maxChunkUpdatesPerTick);
        int budget = AdaptivePerformanceController.scaleBudget(configuredBudget);

        if (dispatchedThisWindow >= budget) {
            rejectedThisWindow++;
            PerformanceTelemetry.skippedChunk();
            return false;
        }

        dispatched.add(renderChunk);
        dispatchedThisWindow++;
        return true;
    }

    public static synchronized void finishUpdateWindow() {
        dispatched.clear();
    }

    public static synchronized int getDispatchedThisWindow() {
        return dispatchedThisWindow;
    }

    public static synchronized int getRejectedThisWindow() {
        return rejectedThisWindow;
    }
}
