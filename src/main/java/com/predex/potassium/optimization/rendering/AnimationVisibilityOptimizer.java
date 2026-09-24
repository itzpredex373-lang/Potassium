package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import java.util.HashSet;
import java.util.Set;

/**
 * Smart-animation admission cache inspired by OptiFine Smart Animations.
 * Callers mark visible animation regions during their render pass, then query
 * shouldAnimate() before doing optional animation work.
 */
public final class AnimationVisibilityOptimizer {
    private static final Set<Long> visibleRegions = new HashSet<Long>();

    private AnimationVisibilityOptimizer() {}

    public static void beginFrame() {
        visibleRegions.clear();
    }

    public static void markVisibleChunk(int chunkX, int chunkZ) {
        if (!PotassiumConfig.enabled || !PotassiumConfig.smartAnimations) return;
        visibleRegions.add(RenderRegionOptimizer.regionKey(chunkX, chunkZ));
    }

    public static boolean shouldAnimate(int chunkX, int chunkZ) {
        if (!PotassiumConfig.enabled || !PotassiumConfig.smartAnimations) return true;
        return visibleRegions.contains(RenderRegionOptimizer.regionKey(chunkX, chunkZ));
    }
}
