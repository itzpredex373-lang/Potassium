package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

/**
 * Entry point for client rendering optimizations.
 *
 * Part 1 starts with conservative, measurable rendering work:
 * distant living entities can be skipped before their renderer is invoked.
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
        if (!PotassiumConfig.enabled || !PotassiumConfig.optimizeEntityRendering) {
            return true;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityLivingBase player = minecraft.thePlayer;

        if (player == null || entity == player) {
            return true;
        }

        double maxDistance = PotassiumConfig.entityRenderDistance;
        return entity.getDistanceSqToEntity(player) <= maxDistance * maxDistance;
    }
}
