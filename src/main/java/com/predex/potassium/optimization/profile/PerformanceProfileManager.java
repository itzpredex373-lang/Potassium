package com.predex.potassium.optimization.profile;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Part 5 runtime profile controller.
 *
 * Profiles are applied once after Forge configuration loading. The selected
 * preset then feeds the real rendering, chunk, particle, CPU and memory
 * optimization paths through PotassiumConfig.
 */
public final class PerformanceProfileManager {
    private static PerformanceProfile activeProfile = PerformanceProfile.LOW_END;

    private PerformanceProfileManager() {}

    public static void applyConfiguredProfile() {
        activeProfile = PerformanceProfile.fromName(
                PotassiumConfig.performanceProfile);

        PotassiumConfig.entityRenderDistance =
                activeProfile.getEntityRenderDistance();

        PotassiumConfig.reduceParticles = true;
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
        PotassiumConfig.adaptivePerformance = true;

        // Deliberately remain false unless explicitly enabled in the config.
        // This optimization can affect entity AI/movement/gameplay.
        PotassiumConfig.reduceEntityUpdates =
                PotassiumConfig.reduceEntityUpdates && false;
    }

    public static PerformanceProfile getActiveProfile() {
        return activeProfile;
    }

    public static String getActiveProfileName() {
        return activeProfile.name();
    }
}
