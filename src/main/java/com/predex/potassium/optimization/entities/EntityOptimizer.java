package com.predex.potassium.optimization.entities;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

/**
 * Part 3 entity optimization.
 *
 * The update throttle is experimental and disabled by default because entity
 * updates can affect AI, movement and gameplay. Rendering optimization remains
 * separate and safe.
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
        if (minecraft.thePlayer == null || entity == minecraft.thePlayer) {
            return true;
        }

        double distance = PotassiumConfig.entityUpdateDistance;
        if (entity.getDistanceSqToEntity(minecraft.thePlayer) <= distance * distance) {
            return true;
        }

        /*
         * Alternate ticks for distant entities instead of running their full
         * update every client tick. This is deliberately conservative.
         */
        return (minecraft.theWorld.getTotalWorldTime() + entity.getEntityId() & 1L) == 0L;
    }
}
