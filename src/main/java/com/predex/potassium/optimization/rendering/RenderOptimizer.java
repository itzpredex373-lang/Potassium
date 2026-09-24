package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public final class RenderOptimizer {
    private RenderOptimizer() {}

    public static boolean isEnabled() {
        return PerformanceManager.isOptimizationEnabled();
    }

    public static boolean isLowMemoryMode() {
        return PerformanceManager.isOptimizationEnabled() && PotassiumConfig.lowMemoryMode;
    }

    public static boolean shouldRenderLivingEntity(EntityLivingBase entity) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.optimizeEntityRendering || entity == null) {
            return true;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityLivingBase player = minecraft.thePlayer;

        if (player == null || entity == player || entity.isDead) {
            return true;
        }

        double maxDistance = AdaptivePerformanceController.scaleDistance(
                PotassiumConfig.entityRenderDistance);
        return entity.getDistanceSqToEntity(player) <= maxDistance * maxDistance;
    }
}