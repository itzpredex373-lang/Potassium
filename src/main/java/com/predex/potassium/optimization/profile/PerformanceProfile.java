package com.predex.potassium.optimization.profile;

public enum PerformanceProfile {
    HIGH(96, 72, 12, 2, 85, 42, true, false),
    MEDIUM(128, 100, 16, 3, 90, 50, false, true),
    LOW(160, 140, 20, 4, 92, 50, false, true),
    PERFORMANCE(64, 50, 8, 1, 75, 35, true, false);

    private final int entityRenderDistance, maxParticlesPerTick, chunkUpdateRadius;
    private final int maxChunkUpdatesPerTick, memoryPressureThreshold, cpuBudgetMillis;
    private final boolean lowMemoryMode, qolAllowed;

    PerformanceProfile(int entityRenderDistance, int maxParticlesPerTick,
                       int chunkUpdateRadius, int maxChunkUpdatesPerTick,
                       int memoryPressureThreshold, int cpuBudgetMillis,
                       boolean lowMemoryMode, boolean qolAllowed) {
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
        if (name == null) return MEDIUM;
        try {
            return valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return MEDIUM;
        }
    }
}
