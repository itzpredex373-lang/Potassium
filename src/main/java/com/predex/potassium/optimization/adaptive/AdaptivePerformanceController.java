package com.predex.potassium.optimization.adaptive;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;

public final class AdaptivePerformanceController {
    private static int qualityPercent = 100;
    private static int pressureTicks;
    private static int recoveryTicks;

    private AdaptivePerformanceController() {}

    public static void update() {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.adaptivePerformance) {
            qualityPercent = 100;
            pressureTicks = 0;
            recoveryTicks = 0;
            return;
        }

        boolean memoryPressure = MemoryOptimizer.isUnderPressure();
        boolean cpuPressure = !CpuOptimizer.shouldRunOptionalWork();
        double averageFrameMs = FrameTimeMonitor.getAverageMs();
        double variance = FrameTimeMonitor.getVarianceMs();
        double onePercentLow = FrameTimeMonitor.getOnePercentLowFps();
        double zeroPointOnePercentLow = FrameTimeMonitor.getZeroPointOnePercentLowFps();

        // Stability is prioritized over peak FPS. A sustained low percentile,
        // frame-time variance, or memory/CPU pressure lowers optional work
        // before the renderer can accumulate a large backlog.
        boolean framePressure = averageFrameMs > 22.0D
                || variance > 90.0D
                || (onePercentLow > 0.0D && onePercentLow < 35.0D)
                || (zeroPointOnePercentLow > 0.0D && zeroPointOnePercentLow < 28.0D);

        boolean severeFramePressure = averageFrameMs > 28.0D
                || variance > 180.0D
                || (onePercentLow > 0.0D && onePercentLow < 25.0D)
                || (zeroPointOnePercentLow > 0.0D && zeroPointOnePercentLow < 20.0D);

        if (memoryPressure || cpuPressure || framePressure) {
            pressureTicks++;
            recoveryTicks = 0;

            if (pressureTicks >= 2) {
                if (MemoryOptimizer.getPressurePercent() >= 95) {
                    qualityPercent = Math.min(qualityPercent, 50);
                } else if (severeFramePressure) {
                    qualityPercent = Math.min(qualityPercent, 60);
                } else if (cpuPressure || framePressure) {
                    qualityPercent = Math.min(qualityPercent, 70);
                } else {
                    qualityPercent = Math.min(qualityPercent, 75);
                }
            }
        } else {
            pressureTicks = 0;
            recoveryTicks++;

            // Recover slowly so quality does not oscillate up/down around a
            // borderline workload. This protects 1%/0.1% lows.
            if (recoveryTicks >= 30) {
                qualityPercent = Math.min(100, qualityPercent + 5);
                recoveryTicks = 0;
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
