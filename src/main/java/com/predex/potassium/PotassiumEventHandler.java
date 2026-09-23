package com.predex.potassium;

import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.benchmark.StabilityGuard;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;
import com.predex.potassium.optimization.compat.CompatibilityManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class PotassiumEventHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !PerformanceManager.isEnabled()) return;

        try {
            PerformanceManager.onClientTick();
            AdaptivePerformanceController.update();

            if (PerformanceManager.isMaintenanceTick() && StabilityGuard.allowOptionalWork()) {
                runMaintenance();
            }
            StabilityGuard.reportSuccess();
        } catch (Throwable ignored) {
            StabilityGuard.reportFailure();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END && PerformanceManager.isEnabled()) {
            FrameTimeMonitor.frame();
        }
    }

    private void runMaintenance() {
        // Compatibility detection is cached by the JVM and used as a safe
        // fallback gate for future renderer hooks.
        CompatibilityManager.isForgePresent();
    }
}
