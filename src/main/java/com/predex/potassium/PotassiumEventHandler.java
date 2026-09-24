package com.predex.potassium;

import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.benchmark.StabilityGuard;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import com.predex.potassium.optimization.rendering.RenderFrameCounter;
import com.predex.potassium.optimization.rendering.RenderStateOptimizer;
import com.predex.potassium.optimization.rendering.TextureBindingOptimizer;
import com.predex.potassium.optimization.compat.CompatibilityManager;
import com.predex.potassium.optimization.rendering.AnimationVisibilityOptimizer;
import com.predex.potassium.optimization.rendering.EntityOcclusionOptimizer;
import com.predex.potassium.optimization.rendering.FrustumRenderOptimizer;
import com.predex.potassium.optimization.world.SmoothWorldOptimizer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public final class PotassiumEventHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !PerformanceManager.isEnabled()) return;

        try {
            PerformanceManager.onClientTick();
            AdaptivePerformanceController.update();

            if (PerformanceManager.isOptimizationEnabled()
                    && PerformanceManager.isMaintenanceTick()
                    && StabilityGuard.allowOptionalWork()
                    && SmoothWorldOptimizer.allowOptionalWork()) {
                runMaintenance();
            }
            StabilityGuard.reportSuccess();
        } catch (Throwable ignored) {
            StabilityGuard.reportFailure();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (!PerformanceManager.isEnabled()) return;

        if (event.phase == TickEvent.Phase.START) {
            RenderFrameCounter.beginFrame();
            PerformanceTelemetry.beginFrame();

            if (PerformanceManager.isOptimizationEnabled()) {
                RenderStateOptimizer.beginFrame();
                TextureBindingOptimizer.beginFrame();
                FrustumRenderOptimizer.beginFrame();
                AnimationVisibilityOptimizer.beginFrame();
                EntityOcclusionOptimizer.beginFrame();
            }
        } else if (event.phase == TickEvent.Phase.END) {
            FrameTimeMonitor.frame();
            PerformanceTelemetry.endFrame();
        }
    }

    private void runMaintenance() {
        CompatibilityManager.isForgePresent();
    }
}
