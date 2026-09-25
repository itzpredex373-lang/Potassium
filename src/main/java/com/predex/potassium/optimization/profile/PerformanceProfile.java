package com.predex.potassium.optimization.profile;

/**
 * Part 5 hardware-oriented presets.
 *
 * A profile controls Potassium's own optimization budgets. It never changes
 * vanilla Minecraft settings and never enables the gameplay-affecting entity
 * update throttle.
 */
public enum PerformanceProfile {
    BALANCED(
            128, 100, 16, 3,
            90, 50, false
    ),
    LOW_END(
            96, 80, 12, 2,
            85, 45, true
    ),
    ULTRA_LOW(
            64, 50, 8, 1,
            75, 35, true
    );

    private final int entityRenderDistance;
    private final int maxParticlesPerTick;
    private final int chunkUpdateRadius;
    private final int maxChunkUpdatesPerTick;
    private final int memoryPressureThreshold;
    private final int cpuBudgetMillis;
    private final boolean lowMemoryMode;

    PerformanceProfile(int entityRenderDistance,
                       int maxParticlesPerTick,
                       int chunkUpdateRadius,
                       int maxChunkUpdatesPerTick,
                       int memoryPressureThreshold,
                       int cpuBudgetMillis,
                       boolean lowMemoryMode) {
        this.entityRenderDistance = entityRenderDistance;
        this.maxParticlesPerTick = maxParticlesPerTick;
        this.chunkUpdateRadius = chunkUpdateRadius;
        this.maxChunkUpdatesPerTick = maxChunkUpdatesPerTick;
        this.memoryPressureThreshold = memoryPressureThreshold;
        this.cpuBudgetMillis = cpuBudgetMillis;
        this.lowMemoryMode = lowMemoryMode;
    }

    public int getEntityRenderDistance() {
        return entityRenderDistance;
    }

    public int getMaxParticlesPerTick() {
        return maxParticlesPerTick;
    }

    public int getChunkUpdateRadius() {
        return chunkUpdateRadius;
    }

    public int getMaxChunkUpdatesPerTick() {
        return maxChunkUpdatesPerTick;
    }

    public int getMemoryPressureThreshold() {
        return memoryPressureThreshold;
    }

    public int getCpuBudgetMillis() {
        return cpuBudgetMillis;
    }

    public boolean isLowMemoryMode() {
        return lowMemoryMode;
    }

    public static PerformanceProfile fromName(String name) {
        if (name == null) {
            return ULTRA_LOW;
        }

        try {
            return valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return LOW_END;
        }
    }
}
