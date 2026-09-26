package com.predex.potassium.optimization.profile;

/**
 * Part 5 hardware-oriented presets.
 *
 * HIGH = light Potassium optimization.
 * MEDIUM = balanced optimization + QoL.
 * LOW = stronger optimization + QoL.
 * PERFORMANCE = maximum Potassium workload reduction; QoL is disabled
 * intentionally to keep the profile focused on raw performance.
 */
public enum PerformanceProfile {
    HIGH(
            160, 140, 20, 4,
            92, 50, false, false
    ),
    MEDIUM(
            128, 100, 16, 3,
            90, 50, false, true
    ),
    LOW(
            96, 80, 12, 2,
            85, 45, true, true
    ),
    PERFORMANCE(
            64, 50, 8, 1,
            75, 35, true, false
    );

    private final int entityRenderDistance;
    private final int maxParticlesPerTick;
    private final int chunkUpdateRadius;
    private final int maxChunkUpdatesPerTick;
    private final int memoryPressureThreshold;
    private final int cpuBudgetMillis;
    private final boolean lowMemoryMode;
    private final boolean qolAllowed;

    PerformanceProfile(int entityRenderDistance,
                       int maxParticlesPerTick,
                       int chunkUpdateRadius,
                       int maxChunkUpdatesPerTick,
                       int memoryPressureThreshold,
                       int cpuBudgetMillis,
                       boolean lowMemoryMode,
                       boolean qolAllowed) {
        this.entityRenderDistance = entityRenderDistance;
        this.maxParticlesPerTick = maxParticlesPerTick;
        this.chunkUpdateRadius = chunkUpdateRadius;
        this.maxChunkUpdatesPerTick = maxChunkUpdatesPerTick;
        this.memoryPressureThreshold = memoryPressureThreshold;
        this.cpuBudgetMillis = cpuBudgetMillis;
        this.lowMemoryMode = lowMemoryMode;
        this.qolAllowed = qolAllowed;
    }

    public int getEntityRenderDistance() { return entityRenderDistance; }
    public int getMaxParticlesPerTick() { return maxParticlesPerTick; }
    public int getChunkUpdateRadius() { return chunkUpdateRadius; }
    public int getMaxChunkUpdatesPerTick() { return maxChunkUpdatesPerTick; }
    public int getMemoryPressureThreshold() { return memoryPressureThreshold; }
    public int getCpuBudgetMillis() { return cpuBudgetMillis; }
    public boolean isLowMemoryMode() { return lowMemoryMode; }
    public boolean isQoLAllowed() { return qolAllowed; }

    public static PerformanceProfile fromName(String name) {
        if (name == null) return PERFORMANCE;

        try {
            return valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return PERFORMANCE;
        }
    }
}
