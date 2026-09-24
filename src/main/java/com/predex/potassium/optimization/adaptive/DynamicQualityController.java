package com.predex.potassium.optimization.adaptive;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Frame-time governor for optional rendering/world work.
 * The goal is to reduce spikes rather than chase a single peak-FPS number.
 */
public final class DynamicQualityController {
    private static double frameMillis = 16.67D;
    private static int optionalWorkPercent = 100;

    private DynamicQualityController() {}

    public static void update(double millis) {
        if (millis <= 0.0D || Double.isNaN(millis) || Double.isInfinite(millis)) return;

        frameMillis = frameMillis * 0.85D + millis * 0.15D;

        if (!PotassiumConfig.adaptivePerformance || !PotassiumConfig.smoothFps) {
            optionalWorkPercent = 100;
            return;
        }

        if (frameMillis > 50.0D) optionalWorkPercent = 35;
        else if (frameMillis > 33.0D) optionalWorkPercent = 55;
        else if (frameMillis > 25.0D) optionalWorkPercent = 70;
        else if (frameMillis > 18.0D) optionalWorkPercent = 88;
        else optionalWorkPercent = 100;
    }

    public static boolean allow(int requiredPercent) {
        return optionalWorkPercent >= Math.max(0, Math.min(100, requiredPercent));
    }

    public static int getOptionalWorkPercent() {
        return optionalWorkPercent;
    }

    public static double getSmoothedFrameMillis() {
        return frameMillis;
    }

    public static int scaleBudget(int configured) {
        int base = Math.max(1, configured);
        return Math.max(1, base * optionalWorkPercent / 100);
    }

    public static int scaleDistance(int configured) {
        int base = Math.max(1, configured);
        int scaled = base * (70 + optionalWorkPercent / 3) / 100;
        return Math.max(16, Math.min(base, scaled));
    }
}
