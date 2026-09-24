package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public final class RenderOptimizer {
    private RenderOptimizer() {}

    public static boolean isEnabled() {
        return PerformanceManager.isOptimizationEnabled() && PotassiumConfig.fastRender;
    }

    public static boolean isLowMemoryMode() {
        return isEnabled() && PotassiumConfig.lowMemoryMode;
    }

    /**
     * Central render admission gate used by the RenderManager core hook.
     * Vanilla rendering remains untouched when the optimization is disabled.
     */
    public static boolean shouldRenderEntity(Entity entity) {
        if (!isEnabled() || entity == null) return true;

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null) return true;

        Entity camera = minecraft.getRenderViewEntity();
        if (camera == null || entity == camera || entity.isDead) return true;

        // Cheap distance rejection first.
        if (entity instanceof EntityLivingBase && PotassiumConfig.optimizeEntityRendering) {
            double maxDistance = AdaptivePerformanceController.scaleDistance(
                    PotassiumConfig.entityRenderDistance);
            if (entity.getDistanceSqToEntity(camera) > maxDistance * maxDistance) {
                return false;
            }
        }

        // Reuse the frustum calculated once for this render frame.
        if (!FrustumRenderOptimizer.isVisible(entity)) {
            return false;
        }

        // Only perform ray-trace occlusion when the feature is enabled.
        if (PotassiumConfig.entityOcclusionCulling
                && !EntityOcclusionOptimizer.isVisible(entity)) {
            return false;
        }

        return true;
    }

    public static boolean shouldRenderLivingEntity(EntityLivingBase entity) {
        return shouldRenderEntity(entity);
    }
}
