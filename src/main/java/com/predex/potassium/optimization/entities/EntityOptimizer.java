package com.predex.potassium.optimization.entities;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

/**
 * Part 3 entity optimization.
 *
 * Distant living-entity updates are throttled only when the experimental
 * setting is enabled. The default remains disabled because LivingUpdateEvent
 * cancellation can affect AI and gameplay behavior.
 */
public final class EntityOptimizer {
    private EntityOptimizer() {}

    public static boolean isUpdateThrottleEnabled() {
        return PotassiumConfig.enabled && PotassiumConfig.reduceEntityUpdates;
    }

    public static boolean shouldUpdate(EntityLivingBase entity) {
        if (!isUpdateThrottleEnabled() || entity == null) {
            return true;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.theWorld == null || minecraft.thePlayer == null
                || entity == minecraft.thePlayer) {
            return true;
        }

        double distance = PotassiumConfig.entityUpdateDistance;
        if (entity.getDistanceSqToEntity(minecraft.thePlayer) <= distance * distance) {
            return true;
        }

        return ((minecraft.theWorld.getTotalWorldTime()
                + (long) entity.getEntityId()) & 1L) == 0L;
    }
}
