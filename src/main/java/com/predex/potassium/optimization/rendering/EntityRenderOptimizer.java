package com.predex.potassium.optimization.rendering;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Entity rendering optimization hooks.
 *
 * The hook runs before a living entity renderer performs its normal work.
 * Cancelling here avoids the renderer/model work for entities outside the
 * configured useful render distance.
 */
public final class EntityRenderOptimizer {

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre event) {
        if (!RenderOptimizer.shouldRenderLivingEntity(event.entity)) {
            event.setCanceled(true);
        }
    }
}
