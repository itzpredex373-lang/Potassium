package com.predex.potassium.optimization.rendering;

import com.predex.potassium.optimization.compat.CompatibilityManager;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class EntityRenderOptimizer {
    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre event) {
        // OptiFine replaces/augments parts of the 1.8.9 renderer. Until a
        // dedicated compatibility layer exists, fail open to avoid competing
        // render cancellation hooks.
        if (!CompatibilityManager.allowRiskyHooks()) {
            return;
        }

        if (!RenderVisibilityOptimizer.shouldRender(event.entity)) {
            event.setCanceled(true);
        }
    }
}
