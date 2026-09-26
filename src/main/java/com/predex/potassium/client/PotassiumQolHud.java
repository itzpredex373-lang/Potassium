package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.benchmark.BenchmarkMonitor;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Locale;

/**
 * Lightweight client HUD.
 *
 * The main FPS/average/minimum/ping line is intentionally independent from
 * F3 and from the optimization profile. Extra QoL lines are profile-gated.
 */
public final class PotassiumQolHud {
    private static final long SESSION_START_MILLIS = System.currentTimeMillis();

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL
                || !PotassiumConfig.qolHud) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.fontRendererObj == null) return;

        String[] lines = buildLines(mc);
        if (lines.length == 0) return;

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
        /*
         * Always keep the first line available without F3:
         * FPS: 144  Avr: 132  Mini: 85  Ping: 32ms
         *
         * "Mini" is Potassium's 0.1% low metric, i.e. the worst sampled
         * frame-time-derived FPS value in the current measurement window.
         */
        boolean extraQol = PerformanceProfileManager.isQoLAllowed();

        int count = 1;
        if (extraQol && PotassiumConfig.qolShowLowFps) count++;
        if (extraQol && PotassiumConfig.qolShowFrameTime) count++;
        if (extraQol && PotassiumConfig.qolShowCoordinates && mc.thePlayer != null) count++;
        if (extraQol && PotassiumConfig.qolShowDirection && mc.thePlayer != null) count++;
        if (extraQol && PotassiumConfig.qolShowBiome && mc.thePlayer != null && mc.theWorld != null) count++;
        if (extraQol && PotassiumConfig.qolShowMemory) count++;
        if (extraQol && PotassiumConfig.qolShowSessionTime) count++;

        String[] lines = new String[count];
        int index = 0;

        double averageFps = FrameTimeMonitor.getAverageMs() <= 0.0D
                ? 0.0D : 1000.0D / FrameTimeMonitor.getAverageMs();

        lines[index++] = String.format(Locale.ROOT,
                "FPS: %.0f  Avr: %.0f  Mini: %.0f  Ping: %dms",
                BenchmarkMonitor.getMeasuredFps(),
                averageFps,
                FrameTimeMonitor.getZeroPointOnePercentLowFps(),
                getPing(mc));

        if (extraQol && PotassiumConfig.qolShowLowFps) {
            lines[index++] = String.format(Locale.ROOT,
                    "1%%: %.0f  |  0.1%%: %.0f",
                    FrameTimeMonitor.getOnePercentLowFps(),
                    FrameTimeMonitor.getZeroPointOnePercentLowFps());
        }

        if (extraQol && PotassiumConfig.qolShowFrameTime) {
            lines[index++] = String.format(Locale.ROOT,
                    "Frame: %.2f ms", FrameTimeMonitor.getAverageMs());
        }

        if (extraQol && PotassiumConfig.qolShowCoordinates && mc.thePlayer != null) {
            lines[index++] = String.format(Locale.ROOT, "XYZ: %.1f %.1f %.1f",
                    mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
        }

        if (extraQol && PotassiumConfig.qolShowDirection && mc.thePlayer != null) {
            lines[index++] = "Facing: " + getDirection(mc.thePlayer.rotationYaw);
        }

        if (extraQol && PotassiumConfig.qolShowBiome
                && mc.thePlayer != null && mc.theWorld != null) {
            lines[index++] = "Biome: "
                    + mc.theWorld.getBiomeGenForCoords(mc.thePlayer.getPosition()).biomeName;
        }

        if (extraQol && PotassiumConfig.qolShowMemory) {
            Runtime runtime = Runtime.getRuntime();
            long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024L * 1024L);
            long max = runtime.maxMemory() / (1024L * 1024L);
            lines[index++] = "Memory: " + used + "/" + max + " MB";
        }

        if (extraQol && PotassiumConfig.qolShowSessionTime) {
            long seconds = Math.max(0L,
                    (System.currentTimeMillis() - SESSION_START_MILLIS) / 1000L);
            lines[index++] = String.format(Locale.ROOT, "Session: %02d:%02d:%02d",
                    seconds / 3600L, (seconds / 60L) % 60L, seconds % 60L);
        }

        return lines;
    }

    private int getPing(Minecraft mc) {
        if (mc.thePlayer == null || mc.getNetHandler() == null) return 0;
        net.minecraft.client.network.NetworkPlayerInfo info =
                mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID());
        return info == null ? 0 : Math.max(0, info.getResponseTime());
    }

    private String getDirection(float yaw) {
        float normalized = (yaw % 360.0F + 360.0F) % 360.0F;
        String[] directions = {"South", "South-West", "West", "North-West",
                "North", "North-East", "East", "South-East"};
        return directions[Math.round(normalized / 45.0F) & 7];
    }
}
