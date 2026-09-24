package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Region-aware batching helper inspired by OptiFine Render Regions.
 * It only supplies grouping decisions; actual RenderGlobal integration remains
 * renderer-hook dependent on the legacy 1.8.9 client.
 */
public final class RenderRegionOptimizer {
    private static final int REGION_SHIFT = 4;

    private RenderRegionOptimizer() {}

    public static boolean enabled() {
        return PotassiumConfig.enabled && PotassiumConfig.renderRegions;
    }

    public static int regionX(int chunkX) {
        return chunkX >> REGION_SHIFT;
    }

    public static int regionZ(int chunkZ) {
        return chunkZ >> REGION_SHIFT;
    }

    public static long regionKey(int chunkX, int chunkZ) {
        return (((long) regionX(chunkX)) << 32)
                ^ (regionZ(chunkZ) & 0xFFFFFFFFL);
    }

    public static boolean sameRegion(int chunkX1, int chunkZ1, int chunkX2, int chunkZ2) {
        return regionX(chunkX1) == regionX(chunkX2)
                && regionZ(chunkZ1) == regionZ(chunkZ2);
    }
}
