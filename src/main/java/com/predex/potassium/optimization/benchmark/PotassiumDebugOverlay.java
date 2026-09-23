package com.predex.potassium.optimization.benchmark;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.compat.CompatibilityManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Adds a small Potassium status block to Minecraft's F3 debug screen.
 *
 * This is diagnostic only; it does not change rendering behaviour.
 */
public final class PotassiumDebugOverlay {

    @SubscribeEvent
    public void onDebugText(RenderGameOverlayEvent.Text event) {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (!minecraft.gameSettings.showDebugInfo) {
            return;
        }

        boolean enabled = PotassiumConfig.enabled;
        boolean render = enabled && PotassiumConfig.optimizeEntityRendering
                && CompatibilityManager.allowRiskyHooks();
        boolean chunk = enabled && PotassiumConfig.optimizeChunkUpdates;
        boolean particles = enabled && PotassiumConfig.reduceParticles;

        event.left.add("Potassium");
        event.left.add("Potassium Render: " + (render ? "ACTIVE" : "OFF"));
        event.left.add("Potassium Culling: " + (render ? "ON" : "OFF"));
        event.left.add("Potassium Chunk: " + (chunk ? "ON" : "OFF"));
        event.left.add("Potassium Particles: " + (particles ? "ON" : "OFF"));
        event.left.add("Potassium Profile: " + PotassiumConfig.performanceProfile);
    }
}
