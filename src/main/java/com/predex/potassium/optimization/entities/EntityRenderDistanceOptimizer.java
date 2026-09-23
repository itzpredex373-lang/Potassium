package com.predex.potassium.optimization.entities;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public final class EntityRenderDistanceOptimizer {
    private EntityRenderDistanceOptimizer() {}

    public static boolean withinRange(Entity entity) {
        if (entity == null || !PotassiumConfig.enabled) return true;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null || entity == mc.thePlayer) return true;
        double d = PotassiumConfig.entityRenderDistance;
        return entity.getDistanceSqToEntity(mc.thePlayer) <= d * d;
    }
}