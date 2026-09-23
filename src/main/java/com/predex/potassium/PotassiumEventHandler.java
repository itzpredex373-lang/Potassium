package com.predex.potassium;

import com.predex.potassium.optimization.PerformanceManager;
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

        if (PerformanceManager.isMaintenanceTick()) {
            runMaintenance();
        }
    }

    private void runMaintenance() {
        // Intentionally allocation-free. Future optimization modules can use
        // this point for measured, low-cost periodic maintenance.
    }
}
