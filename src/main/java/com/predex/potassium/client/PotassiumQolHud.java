package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.benchmark.BenchmarkMonitor;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Locale;

/**
 * Lightweight optional QoL HUD. It is independent from Potassium's
 * optimization master switch and only renders the enabled information.
 */
public final class PotassiumQolHud {
    private static final long SESSION_START_MILLIS = System.currentTimeMillis();

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL
                || !PerformanceProfileManager.isQoLAllowed()
                || !PotassiumConfig.qolHud) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.fontRendererObj == null) return;

        String[] lines = buildLines(mc);
        if (lines.length == 0) return;

        new ScaledResolution(mc);
        float scale = PotassiumConfig.qolHudScale / 100.0F;

        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1.0F);

        int x = 4;
        int y = 4;
        for (String line : lines) {
            mc.fontRendererObj.drawStringWithShadow(line, x, y, 0xFFFFFF);
            y += 10;
        }

        GlStateManager.popMatrix();
    }

    private String[] buildLines(Minecraft mc) {
        int count = 0;
        if (PotassiumConfig.qolShowFps) count++;
        if (PotassiumConfig.qolShowLowFps) count++;
        if (PotassiumConfig.qolShowFrameTime) count++;
        if (PotassiumConfig.qolShowCoordinates && mc.thePlayer != null) count++;
        if (PotassiumConfig.qolShowDirection && mc.thePlayer != null) count++;
        if (PotassiumConfig.qolShowBiome && mc.thePlayer != null && mc.theWorld != null) count++;
        if (PotassiumConfig.qolShowMemory) count++;
        if (PotassiumConfig.qolShowSessionTime) count++;

        String[] lines = new String[count];
        int index = 0;

        if (PotassiumConfig.qolShowFps)
            lines[index++] = String.format(Locale.ROOT, "FPS: %.0f", BenchmarkMonitor.getMeasuredFps());

        if (PotassiumConfig.qolShowLowFps)
            lines[index++] = String.format(Locale.ROOT, "1%%: %.0f  |  0.1%%: %.0f",
                    FrameTimeMonitor.getOnePercentLowFps(),
                    FrameTimeMonitor.getZeroPointOnePercentLowFps());

        if (PotassiumConfig.qolShowFrameTime)
            lines[index++] = String.format(Locale.ROOT, "Frame: %.2f ms",
                    FrameTimeMonitor.getAverageMs());

        if (PotassiumConfig.qolShowCoordinates && mc.thePlayer != null)
            lines[index++] = String.format(Locale.ROOT, "XYZ: %.1f %.1f %.1f",
                    mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);

        if (PotassiumConfig.qolShowDirection && mc.thePlayer != null)
            lines[index++] = "Facing: " + getDirection(mc.thePlayer.rotationYaw);

        if (PotassiumConfig.qolShowBiome && mc.thePlayer != null && mc.theWorld != null)
            lines[index++] = "Biome: " +
                    mc.theWorld.getBiomeGenForCoords(mc.thePlayer.getPosition()).biomeName;

        if (PotassiumConfig.qolShowMemory) {
            Runtime runtime = Runtime.getRuntime();
            long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);
            long max = runtime.maxMemory() / (1024L * 1024L);
            lines[index++] = "Memory: " + used + "/" + max + " MB";
        }

        if (PotassiumConfig.qolShowSessionTime) {
            long seconds = Math.max(0L,
                    (System.currentTimeMillis() - SESSION_START_MILLIS) / 1000L);
            lines[index++] = String.format(Locale.ROOT, "Session: %02d:%02d:%02d",
                    seconds / 3600L, (seconds / 60L) % 60L, seconds % 60L);
        }

        return lines;
    }

    private String getDirection(float yaw) {
        float normalized = (yaw % 360.0F + 360.0F) % 360.0F;
        String[] directions = {"South", "South-West", "West", "North-West",
                "North", "North-East", "East", "South-East"};
        return directions[Math.round(normalized / 45.0F) & 7];
    }
}
