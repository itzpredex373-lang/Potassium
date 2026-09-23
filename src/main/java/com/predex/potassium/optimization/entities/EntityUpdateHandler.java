package com.predex.potassium.optimization.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Client-only hook for the experimental distant-entity update throttle.
 */
public final class EntityUpdateHandler {

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.entityLiving;

        if (!EntityOptimizer.shouldUpdate(entity)) {
            event.setCanceled(true);
        }
    }
}
