package com.predex.potassium.optimization.adaptive;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;

/**
 * Stability-first governor for optional Potassium workloads.
 *
 * Changes are intentionally small and hysteretic so the controller does not
 * create an FPS saw-tooth by repeatedly adding and removing work.
 */
public final class AdaptivePerformanceController {
    private static int qualityPercent = 100;
    private static int pressureTicks;
    private static int recoveryTicks;
    private static int cooldownTicks;

    private AdaptivePerformanceController() {}

    public static void update() {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.adaptivePerformance) {
            qualityPercent = 100;
            pressureTicks = 0;
            recoveryTicks = 0;
            cooldownTicks = 0;
            return;
        }

        boolean memoryPressure = MemoryOptimizer.isUnderPressure();
        boolean cpuPressure = !CpuOptimizer.shouldRunOptionalWork();

        double averageFrameMs = FrameTimeMonitor.getAverageMs();
        double variance = FrameTimeMonitor.getVarianceMs();
        double onePercentLow = FrameTimeMonitor.getOnePercentLowFps();
        double zeroPointOnePercentLow = FrameTimeMonitor.getZeroPointOnePercentLowFps();

        boolean framePressure = averageFrameMs > 22.0D
                || variance > 90.0D
                || (onePercentLow > 0.0D && onePercentLow < 35.0D)
                || (zeroPointOnePercentLow > 0.0D && zeroPointOnePercentLow < 28.0D);

        boolean severeFramePressure = averageFrameMs > 28.0D
                || variance > 180.0D
                || (onePercentLow > 0.0D && onePercentLow < 25.0D)
                || (zeroPointOnePercentLow > 0.0D && zeroPointOnePercentLow < 20.0D);

        boolean pressure = memoryPressure || cpuPressure || framePressure;

        if (pressure) {
            pressureTicks++;
            recoveryTicks = 0;

            if (cooldownTicks > 0) {
                cooldownTicks--;
            }

            // Require sustained pressure. One isolated bad frame should not
            // immediately reduce rendering work.
            if (pressureTicks >= 4 && cooldownTicks == 0) {
                int target;
                if (MemoryOptimizer.getPressurePercent() >= 95) {
                    target = 65;
                } else if (severeFramePressure) {
                    target = 70;
                } else {
                    target = 80;
                }

                if (qualityPercent > target) {
                    qualityPercent = Math.max(target, qualityPercent - 5);
                }

                pressureTicks = 0;
                cooldownTicks = 8;
            }
        } else {
            pressureTicks = 0;

            if (cooldownTicks > 0) {
                cooldownTicks--;
            }

            recoveryTicks++;

            // Recover much more slowly than we reduce work. This prevents
            // pressure/recovery oscillation while the scene is borderline.
            if (recoveryTicks >= 60 && cooldownTicks == 0) {
                qualityPercent = Math.min(100, qualityPercent + 2);
                recoveryTicks = 0;
                cooldownTicks = 8;
            }
        }
    }

    public static int getQualityPercent() {
        return qualityPercent;
    }

    public static int scaleBudget(int configured) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.adaptivePerformance) {
            return Math.max(1, configured);
        }

        int scaled = configured * qualityPercent / 100;
        return Math.max(1, HardwareWorkScaler.scale(scaled));
    }

    public static int scaleDistance(int configured) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.adaptivePerformance) {
            return Math.max(1, configured);
        }

        int scaled = configured * (70 + qualityPercent / 3) / 100;
        return Math.max(16, Math.min(configured, scaled));
    }
}
