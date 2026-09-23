package com.predex.potassium;

import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Client-side scheduler for lightweight Potassium maintenance.
 */
public final class PotassiumEventHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!PerformanceManager.isEnabled()) {
            return;
        }

        PerformanceManager.onClientTick();
        AdaptivePerformanceController.update();

        if (PerformanceManager.isMaintenanceTick()) {
            runMaintenance();
        }
    }

    private void runMaintenance() {
        // Keep periodic maintenance intentionally allocation-free.
        // Adaptive state is updated every tick because it controls optional
        // work budgets across the rendering/chunk/particle engines.
    }
}