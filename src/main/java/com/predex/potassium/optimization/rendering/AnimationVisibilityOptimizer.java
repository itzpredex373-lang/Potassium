package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import java.util.HashSet;
import java.util.Set;

public final class AnimationVisibilityOptimizer {
    private static final Set<Long> visibleRegions = new HashSet<Long>();

    private AnimationVisibilityOptimizer() {}

    public static void beginFrame() {
        visibleRegions.clear();
    }

    public static void markVisibleChunk(int chunkX, int chunkZ) {
        if (!PerformanceManager.isOptimizationEnabled() || !PotassiumConfig.smartAnimations) return;
        visibleRegions.add(RenderRegionOptimizer.regionKey(chunkX, chunkZ));
    }

    public static boolean shouldAnimate(int chunkX, int chunkZ) {
        if (!PerformanceManager.isOptimizationEnabled() || !PotassiumConfig.smartAnimations) return true;
        return visibleRegions.contains(RenderRegionOptimizer.regionKey(chunkX, chunkZ));
    }
}