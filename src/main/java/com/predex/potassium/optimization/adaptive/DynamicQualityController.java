package com.predex.potassium.optimization.adaptive;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Frame-time governor for optional rendering/world work.
 * The goal is to reduce spikes rather than chase a single peak-FPS number.
 */
public final class DynamicQualityController {
    private static double frameMillis = 16.67D;
    private static int optionalWorkPercent = 100;
    private static int pressureFrames;
    private static int recoveryFrames;

    private DynamicQualityController() {}

    public static void update(double millis) {
        if (millis <= 0.0D || Double.isNaN(millis) || Double.isInfinite(millis)) return;

        frameMillis = frameMillis * 0.85D + millis * 0.15D;

        if (!PotassiumConfig.adaptivePerformance || !PotassiumConfig.smoothFps) {
            optionalWorkPercent = 100;
            pressureFrames = 0;
            recoveryFrames = 0;
            return;
        }

        if (frameMillis > 22.0D) {
            pressureFrames++;
            recoveryFrames = 0;
        } else if (frameMillis < 16.5D) {
            recoveryFrames++;
            pressureFrames = 0;
        } else {
            pressureFrames = Math.max(0, pressureFrames - 1);
            recoveryFrames = Math.max(0, recoveryFrames - 1);
        }

        // Hysteresis prevents rapid quality flapping around a frame-time boundary.
        if (pressureFrames >= 3) {
            if (frameMillis > 50.0D) optionalWorkPercent = Math.max(35, optionalWorkPercent - 20);
            else if (frameMillis > 33.0D) optionalWorkPercent = Math.max(50, optionalWorkPercent - 15);
            else if (frameMillis > 25.0D) optionalWorkPercent = Math.max(65, optionalWorkPercent - 10);
            else optionalWorkPercent = Math.max(80, optionalWorkPercent - 5);
            pressureFrames = 0;
        } else if (recoveryFrames >= 20) {
            optionalWorkPercent = Math.min(100, optionalWorkPercent + 5);
            recoveryFrames = 0;
        }
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
