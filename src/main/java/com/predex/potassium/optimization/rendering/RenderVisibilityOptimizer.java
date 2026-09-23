package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public final class RenderVisibilityOptimizer {
    private RenderVisibilityOptimizer() {}

    public static boolean shouldRender(Entity entity) {
        if (!PotassiumConfig.enabled || entity == null) {
            return true;
        }

        if (entity.isDead) {
            return false;
        }

        if (entity instanceof EntityLivingBase
                && !RenderOptimizer.shouldRenderLivingEntity((EntityLivingBase) entity)) {
            return false;
        }

        return FrustumRenderOptimizer.isVisible(entity);
    }
}
