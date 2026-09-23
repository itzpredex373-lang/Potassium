package com.predex.potassium.optimization.system;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Part 4 client system scheduler.
 */
public final class SystemPerformanceScheduler {

    @SubscribeEvent
    public void onClientTickStart(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            CpuOptimizer.beginTick();
        }
    }

    @SubscribeEvent
    public void onClientTickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        MemoryOptimizer.update();
        CpuOptimizer.endTick();
    }
}
