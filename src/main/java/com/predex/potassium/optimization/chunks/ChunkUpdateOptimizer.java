package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.rendering.OptiFinePerformanceParity;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.world.WorldUpdateOptimizer;
import net.minecraft.client.Minecraft;

public final class ChunkUpdateOptimizer {
    private static long tick;
    private static int updatesThisTick;

    private ChunkUpdateOptimizer() {}

    public static void beginTick() {
        tick++;
        updatesThisTick = 0;
    }

    public static boolean tryAcquireUpdateSlot() {
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeChunkUpdates) return true;
        if (!CpuOptimizer.shouldRunOptionalWork()) return false;
        int budget = getEffectiveBudget();
        if (updatesThisTick >= budget) return false;
        updatesThisTick++;
        return true;
    }

    public static boolean shouldProcessChunk(int chunkX, int chunkZ,
                                             int playerChunkX, int playerChunkZ) {
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeChunkUpdates) return true;
        if (!WorldUpdateOptimizer.shouldProcessOptionalChunkWork(chunkX, chunkZ)) return false;
        if (!ChunkOptimizer.isChunkUseful(chunkX, chunkZ, playerChunkX, playerChunkZ,
                PotassiumConfig.chunkUpdateRadius)) return false;
        return tryAcquireUpdateSlot();
    }

    private static int getEffectiveBudget() {
        int configured = Math.max(1, PotassiumConfig.maxChunkUpdatesPerTick);
        if (PotassiumConfig.adaptivePerformance) {
            configured = AdaptivePerformanceController.scaleBudget(configured);
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        boolean standingStill = minecraft.thePlayer != null
                && Math.abs(minecraft.thePlayer.motionX) < 0.001D
                && Math.abs(minecraft.thePlayer.motionZ) < 0.001D;
        boolean localWorld = minecraft.isIntegratedServerRunning();

        return OptiFinePerformanceParity.getChunkBudget(
                configured, standingStill, localWorld);
    }

    public static int getUpdatesThisTick() { return updatesThisTick; }
    public static int getEffectiveBudgetForDiagnostics() { return getEffectiveBudget(); }
    public static long getTick() { return tick; }
}
