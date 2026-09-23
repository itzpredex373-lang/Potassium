package com.predex.potassium.optimization.benchmark;

import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.adaptive.HardwareProfileDetector;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;

/**
 * Small allocation-free diagnostics snapshot for debugging and benchmarking.
 *
 * No FPS value is fabricated here: FPS must come from the actual client
 * renderer/launcher. These metrics expose the conditions Potassium observed.
 */
public final class PerformanceMetrics {
    private PerformanceMetrics() {}

    public static double getLastTickMillis() {
        return CpuOptimizer.getLastTickMillis();
    }

    public static double getAverageTickMillis() {
        return CpuOptimizer.getAverageTickMillis();
    }

    public static int getMemoryPressurePercent() {
        return MemoryOptimizer.getPressurePercent();
    }

    public static int getQualityPercent() {
        return AdaptivePerformanceController.getQualityPercent();
    }

    public static int getProcessorCount() {
        return HardwareProfileDetector.getProcessorCount();
    }

    public static long getMaxHeapMb() {
        return HardwareProfileDetector.getMaxHeapMb();
    }

    public static String getHardwareTier() {
        return HardwareProfileDetector.getTierName();
    }
}
