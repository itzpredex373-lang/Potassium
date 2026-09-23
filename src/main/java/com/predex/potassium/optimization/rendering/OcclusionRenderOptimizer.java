package com.predex.potassium.optimization.rendering;

import net.minecraft.entity.Entity;

/**
 * Conservative occlusion hook.
 *
 * Real depth-buffer occlusion is not safe to emulate from a Forge render event
 * alone. This helper therefore fails open until a renderer/coremod integration
 * can provide reliable visibility data.
 */
public final class OcclusionRenderOptimizer {
    private OcclusionRenderOptimizer() {}

    public static boolean isPotentiallyVisible(Entity entity) {
        // Never incorrectly hide an entity. Frustum/distance culling handles
        // the cheap, safe visibility decisions elsewhere.
        return true;
    }
}
