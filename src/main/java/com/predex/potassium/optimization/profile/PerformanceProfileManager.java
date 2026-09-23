package com.predex.potassium.optimization.profile;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Applies Potassium's selected low-end performance preset.
 */
public final class PerformanceProfileManager {
    private static PerformanceProfile activeProfile = PerformanceProfile.LOW_END;

    private PerformanceProfileManager() {}

    public static void applyConfiguredProfile() {
        PerformanceProfile profile = PerformanceProfile.fromName(
                PotassiumConfig.performanceProfile);

        activeProfile = profile;

        PotassiumConfig.entityRenderDistance = profile.getEntityRenderDistance();
        PotassiumConfig.reduceParticles = profile.isReduceParticles();
        PotassiumConfig.entityUpdateDistance = profile.getEntityUpdateDistance();
        PotassiumConfig.maxParticlesPerTick = profile.getMaxParticlesPerTick();
        PotassiumConfig.chunkUpdateRadius = profile.getChunkUpdateRadius();
        PotassiumConfig.maxChunkUpdatesPerTick = profile.getMaxChunkUpdatesPerTick();
        PotassiumConfig.adaptivePerformance = profile.isAdaptivePerformance();
    }

    public static PerformanceProfile getActiveProfile() {
        return activeProfile;
    }
}
