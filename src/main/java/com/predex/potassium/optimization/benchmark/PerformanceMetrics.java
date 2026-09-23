package com.predex.potassium.optimization.benchmark;

import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.adaptive.HardwareProfileDetector;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;

public final class PerformanceMetrics {
    private PerformanceMetrics() {}

    public static double getLastTickMillis() { return CpuOptimizer.getLastTickMillis(); }
    public static double getAverageTickMillis() { return CpuOptimizer.getAverageTickMillis(); }
    public static double getFrameTimeMillis() { return FrameTimeMonitor.getAverageMs(); }
    public static double getChunkTimeMillis() { return ChunkTimeTracker.getAverageMs(); }
    public static double getMeasuredFps() { return BenchmarkMonitor.getMeasuredFps(); }
    public static int getMemoryPressurePercent() { return MemoryOptimizer.getPressurePercent(); }
    public static int getMeasuredMemoryPercent() { return MemoryMonitor.getUsedPercent(); }
    public static long getEstimatedAllocations() { return AllocationCount(); }
    public static int getQualityPercent() { return AdaptivePerformanceController.getQualityPercent(); }
    public static int getProcessorCount() { return HardwareProfileDetector.getProcessorCount(); }
    public static long getMaxHeapMb() { return HardwareProfileDetector.getMaxHeapMb(); }
    public static String getHardwareTier() { return HardwareProfileDetector.getTierName(); }

    private static long AllocationCount() {
        return com.predex.potassium.optimization.memory.AllocationTracker.getAllocations();
    }
}
