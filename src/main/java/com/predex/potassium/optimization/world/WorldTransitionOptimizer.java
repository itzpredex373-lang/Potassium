package com.predex.potassium.optimization.world;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;

/**
 * Short-lived client workload governor used while entering/leaving a world,
 * changing dimensions, teleporting between server lobbies, or receiving a
 * fresh chunk stream.
 *
 * The goal is to prevent the first burst of world/render work from monopolizing
 * the render thread. Work ramps back up gradually once the new world is warm.
 */
public final class WorldTransitionOptimizer {
    private static final int WARMUP_TICKS = 120;
    private static final int HARD_GUARD_TICKS = 20;
    private static int ticksRemaining;
    private static int totalTicks;

    private WorldTransitionOptimizer() {}

    public static synchronized void beginTransition() {
        ticksRemaining = WARMUP_TICKS;
        totalTicks = 0;
    }

    public static synchronized void endTransition() {
        ticksRemaining = 0;
        totalTicks = 0;
    }

    public static synchronized void tick() {
        if (ticksRemaining <= 0) {
            return;
        }
        totalTicks++;
        ticksRemaining--;
    }

    public static synchronized boolean isActive() {
        return ticksRemaining > 0;
    }

    public static synchronized int getTicksRemaining() {
        return ticksRemaining;
    }

    /**
     * Scales CPU-side chunk work. The first few ticks are intentionally
     * conservative; the budget then ramps toward the normal adaptive value.
     */
    public static synchronized int scaleChunkBudget(int configured) {
        int base = Math.max(1, configured);
        if (!isActive() || !PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.adaptivePerformance) {
            return AdaptivePerformanceController.scaleBudget(base);
        }

        int adaptive = AdaptivePerformanceController.scaleBudget(base);
        if (totalTicks < HARD_GUARD_TICKS) {
            return 1;
        }
        if (totalTicks < 50) {
            return Math.max(1, Math.min(adaptive, 2));
        }
        return adaptive;
    }

    /**
     * Keeps chunk preparation focused near the player during the initial
     * world handoff, then returns to the normal adaptive distance.
     */
    public static synchronized int scaleChunkDistance(int configuredBlocks) {
        int base = Math.max(16, configuredBlocks);
        if (!isActive() || !PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.adaptivePerformance) {
            return AdaptivePerformanceController.scaleDistance(base);
        }

        int adaptive = AdaptivePerformanceController.scaleDistance(base);
        if (totalTicks < HARD_GUARD_TICKS) {
            return Math.max(32, Math.min(adaptive, 64));
        }
        if (totalTicks < 50) {
            return Math.max(48, Math.min(adaptive, 96));
        }
        return adaptive;
    }

    /**
     * Bounded render-thread mesh upload budget for the transition. We avoid
     * a large upload burst while still allowing the new lobby to become
     * visible progressively.
     */
    public static synchronized int scaleMeshUploadBudget(int configured) {
        int base = Math.max(1, configured);
        if (!isActive() || !PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.meshUploadPipeline) {
            return base;
        }

        if (totalTicks < HARD_GUARD_TICKS) {
            return 1;
        }
        if (totalTicks < 50) {
            return Math.min(base, 2);
        }
        return base;
    }
}
