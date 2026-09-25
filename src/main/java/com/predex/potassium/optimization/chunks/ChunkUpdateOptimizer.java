package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.adaptive.DynamicQualityController;
import com.predex.potassium.optimization.rendering.OptiFinePerformanceParity;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;
import net.minecraft.client.Minecraft;

public final class ChunkUpdateOptimizer {
    private static long currentTick;
    private static int processedThisTick;

    private ChunkUpdateOptimizer() {}

    public static void beginTick() {
        currentTick++;
        processedThisTick = 0;
    }

    public static boolean shouldProcessChunk(int chunkX, int chunkZ, int playerChunkX, int playerChunkZ) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.optimizeChunkUpdates) return true;

        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        int distanceSq = dx * dx + dz * dz;
        int radius = AdaptivePerformanceController.scaleDistance(PotassiumConfig.chunkUpdateRadius);

        if (distanceSq > radius * radius) return false;
        if (MemoryOptimizer.isUnderPressure() && distanceSq > 16) return false;

        int configured = OptiFinePerformanceParity.getChunkBudget(
                PotassiumConfig.maxChunkUpdatesPerTick,
                isPlayerStandingStill(),
                isLocalWorld());
        int budget = DynamicQualityController.scaleBudget(configured);

        if (PotassiumConfig.mobileChunkStreaming) {
            if (!MobileChunkStreaming.shouldPrefer(chunkX, chunkZ)) {
                return false;
            }
            budget = Math.min(budget, Math.max(1, MobileChunkStreaming.getBudget()));
        }

        if (processedThisTick >= budget) return false;
        if (!CpuOptimizer.shouldRunOptionalWork()) return false;

        if (PotassiumConfig.mobileChunkStreaming
                && !MobileChunkStreaming.tryAcquireBudget()) {
            return false;
        }

        processedThisTick++;
        return true;
    }

    public static boolean shouldRunRendererUpdate() {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.rendererCoreHooks
                || !PotassiumConfig.optimizeChunkUpdates) {
            return true;
        }

        return DynamicQualityController.allow(35)
                || CpuOptimizer.shouldRunOptionalWork();
    }

    private static boolean isPlayerStandingStill() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer != null
                && Math.abs(mc.thePlayer.motionX) < 0.003D
                && Math.abs(mc.thePlayer.motionZ) < 0.003D;
    }

    private static boolean isLocalWorld() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.isSingleplayer() && mc.theWorld != null;
    }
}
