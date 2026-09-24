package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
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
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeChunkUpdates) return true;

        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        int distanceSq = dx * dx + dz * dz;
        int radius = PotassiumConfig.chunkUpdateRadius;

        if (distanceSq > radius * radius) return false;
        if (MemoryOptimizer.isMemoryPressureHigh() && distanceSq > 16) return false;

        int budget = OptiFinePerformanceParity.getChunkBudget(
                PotassiumConfig.maxChunkUpdatesPerTick,
                isPlayerStandingStill(),
                isLocalWorld());

        if (processedThisTick >= budget) return false;
        processedThisTick++;

        return CpuOptimizer.shouldRunOptionalWork()
                || AdaptivePerformanceController.isPerformanceDegraded();
    }

    /** Real core hook: gates RenderGlobal.updateChunks without replacing vanilla's renderer. */
    public static boolean shouldRunRendererUpdate() {
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeChunkUpdates) return true;
        if (MemoryOptimizer.isMemoryPressureHigh()) return false;
        return CpuOptimizer.shouldRunOptionalWork()
                || AdaptivePerformanceController.isPerformanceDegraded();
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
