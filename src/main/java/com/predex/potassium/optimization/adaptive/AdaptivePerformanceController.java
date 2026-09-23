package com.predex.potassium.optimization.adaptive;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;

/**
 * Part B adaptive quality controller.
 *
 * Adjusts Potassium's optional workload without changing vanilla gameplay
 * settings. The controller uses hysteresis so quality does not oscillate
 * every tick when a device is near a threshold.
 */
public final class AdaptivePerformanceController {
    private static int qualityPercent = 100;
    private static int pressureTicks;
    private static int recoveryTicks;

    private AdaptivePerformanceController() {}

    public static void update() {
        if (!PotassiumConfig.enabled || !PotassiumConfig.adaptivePerformance) {
            qualityPercent = 100;
            pressureTicks = 0;
            recoveryTicks = 0;
            return;
        }

        boolean memoryPressure = MemoryOptimizer.isUnderPressure();
        boolean cpuPressure = !CpuOptimizer.shouldRunOptionalWork();

        if (memoryPressure || cpuPressure) {
            pressureTicks++;
            recoveryTicks = 0;

            if (pressureTicks >= 2) {
                if (MemoryOptimizer.getPressurePercent() >= 95) {
                    qualityPercent = Math.min(qualityPercent, 50);
                } else if (cpuPressure) {
                    qualityPercent = Math.min(qualityPercent, 65);
                } else {
                    qualityPercent = Math.min(qualityPercent, 75);
                }
            }
        } else {
            pressureTicks = 0;
            recoveryTicks++;

            if (recoveryTicks >= 20) {
                qualityPercent = Math.min(100, qualityPercent + 5);
                recoveryTicks = 0;
            }
        }
    }

    public static int getQualityPercent() {
        return qualityPercent;
    }

    public static int scaleBudget(int configured) {
        if (!PotassiumConfig.adaptivePerformance) {
            return Math.max(1, configured);
        }

        return Math.max(1, configured * qualityPercent / 100);
    }

    public static int scaleDistance(int configured) {
        if (!PotassiumConfig.adaptivePerformance) {
            return Math.max(1, configured);
        }

        // Distance is scaled by sqrt-like steps so quality changes do not
        // destroy the visible range as aggressively as linear scaling.
        int scaled = configured * (70 + qualityPercent / 3) / 100;
        return Math.max(16, Math.min(configured, scaled));
    }
}
