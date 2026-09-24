package com.predex.potassium;

import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.benchmark.StabilityGuard;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import com.predex.potassium.optimization.benchmark.PotassiumDevDiagnostics;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraft.client.Minecraft;

public final class PotassiumEventHandler {
    private int devChatTicks;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !PerformanceManager.isEnabled()) return;

        try {
            PerformanceManager.onClientTick();
            if (PotassiumDevDiagnostics.ENABLED) {
                devChatTicks++;
                if (devChatTicks >= 600) {
                    devChatTicks = 0;
                    sendDevStatus();
                }
            }

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
            PotassiumDevDiagnostics.sampleFps(Minecraft.getMinecraft().getDebugFPS());
        }
    }

    @SubscribeEvent
    public void onDebugOverlay(RenderGameOverlayEvent.Text event) {
        if (!PotassiumDevDiagnostics.ENABLED
                || event.type != RenderGameOverlayEvent.ElementType.TEXT) return;
        Minecraft mc = Minecraft.getMinecraft();
        event.left.add("§6Potassium Dev Temp V1");
        event.left.add("§7Profile: §f" + PotassiumDevDiagnostics.getProfile());
        event.left.add("§7Optimization: §f"
                + (PerformanceManager.isOptimizationEnabled() ? "ON" : "OFF"));
        event.left.add("§7ASM E/T/G/B: §f" + PotassiumDevDiagnostics.getAsmStatus());
        event.left.add("§7ASM Fail: §f" + PotassiumDevDiagnostics.asmFailures);
        event.left.add("§7Hooks P/T/C/B: §f"
                + PotassiumDevDiagnostics.particleHookCalls + "/"
                + PotassiumDevDiagnostics.tessellatorHookCalls + "/"
                + PotassiumDevDiagnostics.chunkHookCalls + "/"
                + PotassiumDevDiagnostics.blockHookCalls);
        event.right.add("§7FPS: §f" + mc.getDebugFPS());
        event.right.add("§7Avg: §f" + PotassiumDevDiagnostics.getAverageFps());
        event.right.add("§7Min: §f" + PotassiumDevDiagnostics.getMinimumFps());
        event.right.add("§71% Low: §f" + PotassiumDevDiagnostics.getOnePercentLowFps());
        event.right.add("§7Frame: §f"
                + String.format("%.1f", PerformanceTelemetry.getLastFrameMillis()) + " ms");
        event.right.add("§7OFF Avg: §f" + PotassiumDevDiagnostics.getBaselineAverageFps());
        event.right.add("§7ON Avg: §f" + PotassiumDevDiagnostics.getOptimizedAverageFps());
        event.right.add("§7Δ FPS: §f" + PotassiumDevDiagnostics.getFpsDifference()
                + " (" + String.format("%.1f", PotassiumDevDiagnostics.getFpsGainPercent()) + "%)");
        event.right.add("§7Guard: §f"
                + (StabilityGuard.isDegraded() ? "DEGRADED" : "OK")
                + " (" + StabilityGuard.getFailures() + ")");
    }

    private void sendDevStatus() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;
        mc.thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText(
                "§6[Potassium Dev] §fFPS " + mc.getDebugFPS()
                + " | Avg " + PotassiumDevDiagnostics.getAverageFps()
                + " | 1% Low " + PotassiumDevDiagnostics.getOnePercentLowFps()
                + " | Opt "
                + (PerformanceManager.isOptimizationEnabled() ? "ON" : "OFF")
                + " | OFF " + PotassiumDevDiagnostics.getBaselineAverageFps()
                + " | ON " + PotassiumDevDiagnostics.getOptimizedAverageFps()
                + " | Δ " + PotassiumDevDiagnostics.getFpsDifference()));
    }

    private void runMaintenance() {
        CompatibilityManager.isForgePresent();
    }
}
