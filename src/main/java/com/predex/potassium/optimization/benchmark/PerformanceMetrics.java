package com.predex.potassium.optimization.benchmark;

import com.predex.potassium.optimization.adaptive.AdaptivePerformanceController;
import com.predex.potassium.optimization.adaptive.HardwareProfileDetector;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.system.MemoryOptimizer;
import com.predex.potassium.optimization.chunks.PotassiumMeshUploadQueue;
import com.predex.potassium.optimization.chunks.PotassiumRenderSectionManager;

public final class PerformanceMetrics {
    private PerformanceMetrics() {}

    public static double getLastTickMillis() { return CpuOptimizer.getLastTickMillis(); }
    public static double getAverageTickMillis() { return CpuOptimizer.getAverageTickMillis(); }
    public static double getFrameTimeMillis() { return FrameTimeMonitor.getAverageMs(); }
    public static double getOnePercentLowFps() { return FrameTimeMonitor.getOnePercentLowFps(); }
    public static double getZeroPointOnePercentLowFps() {
        return FrameTimeMonitor.getZeroPointOnePercentLowFps();
    }
    public static double getFrameTimeVariance() { return FrameTimeMonitor.getVarianceMs(); }
    public static double getChunkTimeMillis() { return ChunkTimeTracker.getAverageMs(); }
    public static double getMeasuredFps() { return BenchmarkMonitor.getMeasuredFps(); }
    public static int getMemoryPressurePercent() { return MemoryOptimizer.getPressurePercent(); }
    public static int getMeasuredMemoryPercent() { return MemoryMonitor.getUsedPercent(); }
    public static long getEstimatedAllocations() {
        return com.predex.potassium.optimization.memory.AllocationTracker.getAllocations();
    }
    public static int getQualityPercent() { return AdaptivePerformanceController.getQualityPercent(); }
    public static int getProcessorCount() { return HardwareProfileDetector.getProcessorCount(); }
    public static long getMaxHeapMb() { return HardwareProfileDetector.getMaxHeapMb(); }
    public static String getHardwareTier() { return HardwareProfileDetector.getTierName(); }
    public static int getMeshUploadQueueSize() { return PotassiumMeshUploadQueue.size(); }
    public static int getMeshUploadDropped() { return PotassiumMeshUploadQueue.getDropped(); }
    public static int getMeshUploadFailed() { return PotassiumMeshUploadQueue.getFailed(); }
    public static int getVisibleSections() { return PotassiumRenderSectionManager.getVisibleSections(); }
    public static int getObservedSections() { return PotassiumRenderSectionManager.getObservedSections(); }
    public static int getSectionCount() { return PotassiumRenderSectionManager.getSectionCount(); }
}
