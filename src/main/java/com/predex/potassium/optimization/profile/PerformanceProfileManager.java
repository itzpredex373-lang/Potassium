package com.predex.potassium.optimization.profile;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Part 5 runtime profile controller.
 */
public final class PerformanceProfileManager {
    private static PerformanceProfile activeProfile = PerformanceProfile.PERFORMANCE;

    private PerformanceProfileManager() {}

    public static void applyConfiguredProfile() {
        activeProfile = PerformanceProfile.fromName(
                PotassiumConfig.performanceProfile);

        PotassiumConfig.entityRenderDistance =
                activeProfile.getEntityRenderDistance();

        PotassiumConfig.maxParticlesPerTick =
                activeProfile.getMaxParticlesPerTick();

        PotassiumConfig.chunkUpdateRadius =
                activeProfile.getChunkUpdateRadius();
        PotassiumConfig.maxChunkUpdatesPerTick =
                activeProfile.getMaxChunkUpdatesPerTick();

        PotassiumConfig.memoryPressureThreshold =
                activeProfile.getMemoryPressureThreshold();
        PotassiumConfig.cpuBudgetMillis =
                activeProfile.getCpuBudgetMillis();

        PotassiumConfig.lowMemoryMode =
                activeProfile.isLowMemoryMode();
    }

    public static PerformanceProfile getActiveProfile() {
        return activeProfile;
    }

    public static String getActiveProfileName() {
        return activeProfile.name();
    }

    public static boolean isQoLAllowed() {
        return activeProfile.isQoLAllowed();
    }
}
