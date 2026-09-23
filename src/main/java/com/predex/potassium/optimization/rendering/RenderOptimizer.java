package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

/**
 * Part 1 rendering gate.
 *
 * Performs the cheapest useful test first: squared distance, with no sqrt and
 * no allocations. The local player is always rendered.
 */
public final class RenderOptimizer {
    private RenderOptimizer() {}

    public static boolean isEnabled() {
        return PotassiumConfig.enabled;
    }

    public static boolean isLowMemoryMode() {
        return PotassiumConfig.lowMemoryMode;
    }

    public static boolean shouldRenderLivingEntity(EntityLivingBase entity) {
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeEntityRendering
                || entity == null) {
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