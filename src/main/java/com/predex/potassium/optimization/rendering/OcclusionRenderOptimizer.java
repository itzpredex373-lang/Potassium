package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.entity.Entity;

public final class OcclusionRenderOptimizer {
    private OcclusionRenderOptimizer() {}

    public static boolean isPotentiallyVisible(Entity entity) {
        if (entity == null) return true;
        if (!PotassiumConfig.enabled || !PotassiumConfig.entityOcclusionCulling) return true;
        return EntityOcclusionOptimizer.isVisible(entity);
    }
}
