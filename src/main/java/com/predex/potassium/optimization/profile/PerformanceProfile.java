package com.predex.potassium.optimization.profile;

/**
 * Part 5 preset settings for different hardware targets.
 *
 * Profiles are applied once during client initialization and only change
 * Potassium's own configuration values. They do not modify Minecraft options.
 */
public enum PerformanceProfile {
    BALANCED(
            128, true, 64, 100,
            16, 3, true
    ),
    LOW_END(
            96, true, 64, 80,
            12, 2, true
    ),
    ULTRA_LOW(
            64, true, 48, 50,
            8, 1, true
    );

    private final int entityRenderDistance;
    private final boolean reduceParticles;
    private final int entityUpdateDistance;
    private final int maxParticlesPerTick;
    private final int chunkUpdateRadius;
    private final int maxChunkUpdatesPerTick;
    private final boolean adaptivePerformance;

    PerformanceProfile(int entityRenderDistance,
                       boolean reduceParticles,
                       int entityUpdateDistance,
                       int maxParticlesPerTick,
                       int chunkUpdateRadius,
                       int maxChunkUpdatesPerTick,
                       boolean adaptivePerformance) {
        this.entityRenderDistance = entityRenderDistance;
        this.reduceParticles = reduceParticles;
        this.entityUpdateDistance = entityUpdateDistance;
        this.maxParticlesPerTick = maxParticlesPerTick;
        this.chunkUpdateRadius = chunkUpdateRadius;
        this.maxChunkUpdatesPerTick = maxChunkUpdatesPerTick;
        this.adaptivePerformance = adaptivePerformance;
    }

    public int getEntityRenderDistance() {
        return entityRenderDistance;
    }

    public boolean isReduceParticles() {
        return reduceParticles;
    }

    public int getEntityUpdateDistance() {
        return entityUpdateDistance;
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

    public boolean isAdaptivePerformance() {
        return adaptivePerformance;
    }

    public static PerformanceProfile fromName(String name) {
        if (name == null) {
            return LOW_END;
        }

        try {
            return valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return LOW_END;
        }
    }
}
