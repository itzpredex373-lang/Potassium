package com.predex.potassium.optimization.particles;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Client-side particle maintenance scheduler.
 */
public final class ParticleClientScheduler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ParticleOptimizer.beginTick();
        }
    }
}
