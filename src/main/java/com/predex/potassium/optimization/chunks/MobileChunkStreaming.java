package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.client.renderer.chunk.RenderChunk;

/**
 * Mobile-first chunk streaming governor.
 *
 * It does not change Minecraft's configured render distance. Instead it
 * controls which optional rebuilds are admitted, predicts the player's next
 * chunk from motion, and protects the render thread with a small workload
 * budget.
 */
public final class MobileChunkStreaming {
    private static int playerChunkX;
    private static int playerChunkZ;
    private static int predictedChunkX;
    private static int predictedChunkZ;
    private static double motionX;
    private static double motionZ;
    private static double speed;
    private static boolean initialized;

    private static int usedBudget;
    private static int budget;
    private static long lastTick;

    private MobileChunkStreaming() {}

    public static void beginTick() {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.optimizeChunkUpdates
                || !PotassiumConfig.mobileChunkStreaming) {
            usedBudget = 0;
            budget = Math.max(1, PotassiumConfig.maxChunkUpdatesPerTick);
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        Entity player = mc.thePlayer;
        if (player == null) {
            initialized = false;
            usedBudget = 0;
            budget = 1;
            return;
        }

        playerChunkX = ((int) Math.floor(player.posX)) >> 4;
        playerChunkZ = ((int) Math.floor(player.posZ)) >> 4;

        motionX = clampMotion(player.motionX);
        motionZ = clampMotion(player.motionZ);
        speed = Math.sqrt(motionX * motionX + motionZ * motionZ);

        // Predict roughly 1-3 chunks ahead, but never more than the configured
        // streaming radius. The prediction is intentionally conservative so
        // it does not create a large speculative rebuild queue.
        int prediction = speed > 0.55D ? 3 : (speed > 0.20D ? 2 : 1);
        prediction = Math.min(prediction,
                Math.max(1, PotassiumConfig.movementPredictionChunks));

        predictedChunkX = playerChunkX
                + signToInt(motionX) * prediction;
        predictedChunkZ = playerChunkZ
                + signToInt(motionZ) * prediction;

        budget = calculateBudget();
        usedBudget = 0;
        initialized = true;
        lastTick++;
    }

    private static int calculateBudget() {
        int configured = Math.max(1, PotassiumConfig.mobileChunkLoadBudget);
        configured = Math.min(configured,
                Math.max(1, PotassiumConfig.maxChunkUpdatesPerTick));

        if (MemoryOptimizer.isUnderPressure()
                || !CpuOptimizer.shouldRunOptionalWork()) {
            return 1;
        }

        double averageMs = FrameTimeMonitor.getAverageMs();
        double variance = FrameTimeMonitor.getVarianceMs();

        // A single bad frame must not collapse streaming. Only sustained
        // average/variance pressure reduces the mobile budget.
        if (averageMs > 28.0D || variance > 180.0D) {
            return 1;
        }

        if (speed > 0.55D) {
            return 1;
        }

        if (speed > 0.20D) {
            return Math.min(configured, 2);
        }

        return configured;
    }

    public static boolean tryAcquireBudget() {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.optimizeChunkUpdates
                || !PotassiumConfig.mobileChunkStreaming) {
            return true;
        }

        if (usedBudget >= Math.max(1, budget)) {
            return false;
        }

        usedBudget++;
        return true;
    }

    /**
     * Returns true for chunks close to the player's predicted movement path.
     * Very close chunks are always admitted; farther chunks are preferentially
     * admitted when they are in front of the player.
     */
    public static boolean shouldPrefer(RenderChunk renderChunk) {
        if (renderChunk == null) return true;
        try {
            BlockPos pos = renderChunk.getPosition();
            return pos == null || shouldPrefer(pos.getX() >> 4, pos.getZ() >> 4);
        } catch (Throwable ignored) {
            return true;
        }
    }

    public static boolean shouldPrefer(int x, int z) {
        if (!initialized) return true;

        int dx = x - playerChunkX;
        int dz = z - playerChunkZ;
        int distanceSq = dx * dx + dz * dz;

        // Never starve the immediate neighborhood.
        if (distanceSq <= 2) return true;

        int predictedDx = x - predictedChunkX;
        int predictedDz = z - predictedChunkZ;
        int predictedDistanceSq = predictedDx * predictedDx
                + predictedDz * predictedDz;

        if (predictedDistanceSq <= 2) return true;

        if (speed < 0.08D) {
            return distanceSq <= 4;
        }

        // Directional streaming: while moving, allow a wider admission cone
        // ahead and a smaller cone behind the player.
        double dot = dx * motionX + dz * motionZ;
        boolean ahead = dot > 0.0D;

        if (ahead) {
            return distanceSq <= 8;
        }

        return distanceSq <= 4;
    }

    public static int getPlayerChunkX() {
        return playerChunkX;
    }

    public static int getPlayerChunkZ() {
        return playerChunkZ;
    }

    public static int getPredictedChunkX() {
        return predictedChunkX;
    }

    public static int getPredictedChunkZ() {
        return predictedChunkZ;
    }

    public static double getSpeed() {
        return speed;
    }

    public static int getBudget() {
        return budget;
    }

    public static int getUsedBudget() {
        return usedBudget;
    }

    public static boolean isMoving() {
        return speed > 0.08D;
    }

    private static double clampMotion(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return 0.0D;
        if (value > 1.5D) return 1.5D;
        if (value < -1.5D) return -1.5D;
        return value;
    }

    private static int signToInt(double value) {
        if (value > 0.035D) return 1;
        if (value < -0.035D) return -1;
        return 0;
    }
}
