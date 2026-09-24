package com.predex.potassium.optimization.entities;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public final class EntityOptimizer {
    private EntityOptimizer() {}

    public static boolean isUpdateThrottleEnabled() {
        return PerformanceManager.isOptimizationEnabled() && PotassiumConfig.reduceEntityUpdates;
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
