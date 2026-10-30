package com.predex.potassium;

import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import com.predex.potassium.optimization.compat.CompatibilityManager;
import com.predex.potassium.optimization.world.WorldTransitionOptimizer;
import com.predex.potassium.optimization.chunks.PotassiumMeshUploadQueue;
import com.predex.potassium.optimization.chunks.PotassiumChunkMeshBuildQueue;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Minimal client lifecycle and telemetry handler.
 *
 * With OptiFine installed, OptiDex deliberately avoids running its own
 * renderer, mesh, animation, culling, or chunk-upload maintenance paths.
 * Frame-time measurement remains available regardless of the master switch.
 */
public final class PotassiumEventHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        PerformanceManager.onClientTick();
        WorldTransitionOptimizer.tick();

        // The custom mesh builder is only relevant to the standalone renderer.
        if (!CompatibilityManager.isOptiFinePresent()
                && PerformanceManager.isOptimizationEnabled()) {
            PotassiumChunkMeshBuildQueue.start();
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            PerformanceTelemetry.beginFrame();
        } else if (event.phase == TickEvent.Phase.END) {
            // Do not drain OptiDex-owned mesh uploads while OptiFine owns rendering.
            if (!CompatibilityManager.isOptiFinePresent()) {
                PotassiumMeshUploadQueue.drainFrame();
            }
            FrameTimeMonitor.frame();
            PerformanceTelemetry.endFrame();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world != null && !event.world.isRemote) return;
        WorldTransitionOptimizer.beginTransition();
        if (!CompatibilityManager.isOptiFinePresent()) {
            PotassiumMeshUploadQueue.beginWorldGeneration();
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world != null && !event.world.isRemote) return;
        WorldTransitionOptimizer.beginTransition();
        if (!CompatibilityManager.isOptiFinePresent()) {
            PotassiumMeshUploadQueue.beginWorldGeneration();
        }
    }
}
