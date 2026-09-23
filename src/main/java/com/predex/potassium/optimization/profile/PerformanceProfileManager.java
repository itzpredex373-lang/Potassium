package com.predex.potassium.optimization.profile;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Part 5 runtime profile controller.
 *
 * Profiles are applied once after Forge configuration loading. The selected
 * preset feeds Potassium's optimization paths through PotassiumConfig.
 *
 * The profile is a baseline preset: explicit user toggles such as
 * reduceParticles=false are respected instead of being silently overwritten.
 */
public final class PerformanceProfileManager {
    private static PerformanceProfile activeProfile = PerformanceProfile.LOW_END;

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
        PotassiumConfig.adaptivePerformance = true;

        // Do not enable or disable gameplay-affecting entity update throttling.
        // Do not override the user's reduceParticles master toggle.
    }

    public static PerformanceProfile getActiveProfile() {
        return activeProfile;
    }

    public static String getActiveProfileName() {
        return activeProfile.name();
    }
}
