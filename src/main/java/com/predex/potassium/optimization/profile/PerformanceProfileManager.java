package com.predex.potassium.optimization.profile;

import com.predex.potassium.config.PotassiumConfig;

public final class PerformanceProfileManager {
    private static PerformanceProfile activeProfile = PerformanceProfile.MEDIUM;

    private PerformanceProfileManager() {}

    public static void applyConfiguredProfile() {
        activeProfile = PerformanceProfile.fromName(PotassiumConfig.performanceProfile);
        PotassiumConfig.performanceProfile = activeProfile.name();

        PotassiumConfig.entityRenderDistance = activeProfile.getEntityRenderDistance();
        PotassiumConfig.maxParticlesPerTick = activeProfile.getMaxParticlesPerTick();
        PotassiumConfig.chunkUpdateRadius = activeProfile.getChunkUpdateRadius();
        PotassiumConfig.maxChunkUpdatesPerTick = activeProfile.getMaxChunkUpdatesPerTick();
        PotassiumConfig.memoryPressureThreshold = activeProfile.getMemoryPressureThreshold();
        PotassiumConfig.cpuBudgetMillis = activeProfile.getCpuBudgetMillis();
        PotassiumConfig.lowMemoryMode = activeProfile.isLowMemoryMode();

        if (activeProfile == PerformanceProfile.LOW) {
            // More QoL, intentionally lighter optimization.
            PotassiumConfig.fastRender = false;
            PotassiumConfig.fastMath = true;
            PotassiumConfig.lazyChunkLoading = false;
            PotassiumConfig.renderRegions = false;
            PotassiumConfig.smartAnimations = false;
            PotassiumConfig.blockFaceCulling = true;
            PotassiumConfig.entityOcclusionCulling = false;
            PotassiumConfig.rendererCoreHooks = false;
            PotassiumConfig.skipEmptyDrawCalls = true;
            PotassiumConfig.renderSections = false;
            PotassiumConfig.meshUploadPipeline = true;
            PotassiumConfig.customMeshPreparation = false;
            PotassiumConfig.reduceParticles = false;
            PotassiumConfig.optimizeEntityRendering = false;
            PotassiumConfig.optimizeChunkUpdates = true;
            PotassiumConfig.mobileChunkStreaming = false;
        } else {
            // Medium keeps balanced optimization; High/Performance keep QoL disabled and favor performance.
            PotassiumConfig.fastRender = true;
            PotassiumConfig.fastMath = true;
            PotassiumConfig.lazyChunkLoading = true;
            PotassiumConfig.renderRegions = true;
            PotassiumConfig.smartAnimations = true;
            PotassiumConfig.blockFaceCulling = true;
            PotassiumConfig.entityOcclusionCulling = true;
            PotassiumConfig.rendererCoreHooks = true;
            PotassiumConfig.skipEmptyDrawCalls = true;
            PotassiumConfig.renderSections = true;
            PotassiumConfig.meshUploadPipeline = true;
            PotassiumConfig.customMeshPreparation = true;
            PotassiumConfig.reduceParticles = true;
            PotassiumConfig.optimizeEntityRendering = true;
            PotassiumConfig.optimizeChunkUpdates = true;
            PotassiumConfig.mobileChunkStreaming = true;
        }

        if (activeProfile == PerformanceProfile.PERFORMANCE) {
            PotassiumConfig.maxMeshUploadsPerFrame = 1;
            PotassiumConfig.maxEntityOcclusionTestsPerFrame = 32;
        } else if (activeProfile == PerformanceProfile.HIGH) {
            PotassiumConfig.maxMeshUploadsPerFrame = 2;
            PotassiumConfig.maxEntityOcclusionTestsPerFrame = 48;
        } else if (activeProfile == PerformanceProfile.MEDIUM) {
            PotassiumConfig.maxMeshUploadsPerFrame = 3;
            PotassiumConfig.maxEntityOcclusionTestsPerFrame = 64;
        } else if (activeProfile == PerformanceProfile.LOW) {
            PotassiumConfig.maxMeshUploadsPerFrame = 2;
            PotassiumConfig.maxEntityOcclusionTestsPerFrame = 48;
        }
    }

    public static PerformanceProfile getActiveProfile() { return activeProfile; }
    public static String getActiveProfileName() { return activeProfile.name(); }
    public static boolean isQoLAllowed() { return activeProfile.isQoLAllowed(); }
    public static boolean isPetAllowed() { return activeProfile == PerformanceProfile.LOW; }

    /**
     * Returns whether the active profile intentionally restricts extra QoL.
     * HIGH and PERFORMANCE keep the performance-first behavior.
     */
    public static boolean isPerformanceRestricted() {
        return activeProfile == PerformanceProfile.HIGH
                || activeProfile == PerformanceProfile.PERFORMANCE;
    }

    /**
     * Re-apply the persisted profile after config reloads or external config edits.
     * This keeps profile-owned budgets and feature switches internally consistent.
     */
    public static void reloadConfiguredProfile() {
        applyConfiguredProfile();
        if (!isPetAllowed()) {
            PotassiumConfig.miniPetEnabled = false;
        }
    }
}
