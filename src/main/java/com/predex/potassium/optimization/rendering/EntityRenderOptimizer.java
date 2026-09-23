package com.predex.potassium.optimization.rendering;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Entity rendering optimization hooks.
 *
 * Distance and frustum tests run before normal living-entity renderer work.
 */
public final class EntityRenderOptimizer {
    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre event) {
        if (!RenderVisibilityOptimizer.shouldRender(event.entity)) {
            event.setCanceled(true);
        }
    }
}
