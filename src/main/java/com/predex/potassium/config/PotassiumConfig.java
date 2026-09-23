package com.predex.potassium.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public final class PotassiumConfig {
    private static Configuration configuration;

    public static boolean enabled = true;
    public static boolean lowMemoryMode = true;
    public static boolean reduceParticles = true;
    public static boolean reduceEntityUpdates = false;
    public static boolean optimizeEntityRendering = true;
    public static int entityRenderDistance = 96;
    public static boolean optimizeChunkUpdates = true;
    public static int chunkUpdateRadius = 12;
    public static int maxChunkUpdatesPerTick = 2;
    public static int entityUpdateDistance = 64;
    public static int maxParticlesPerTick = 80;

    private PotassiumConfig() {}

    public static void init(File file) {
        configuration = new Configuration(file);
        load();
    }

    public static void load() {
        if (configuration == null) return;
        configuration.load();

        enabled = configuration.getBoolean("enabled", "general", true,
                "Master switch for Potassium optimizations.");
        lowMemoryMode = configuration.getBoolean("lowMemoryMode", "performance", true,
                "Conservative settings intended for low-memory devices.");
        reduceParticles = configuration.getBoolean("reduceParticles", "performance", true,
                "Allows Potassium to reduce unnecessary particle work.");
        reduceEntityUpdates = configuration.getBoolean("reduceEntityUpdates", "performance", false,
                "Experimental entity-update optimization. Disabled by default.");

        optimizeEntityRendering = configuration.getBoolean("optimizeEntityRendering", "rendering", true,
                "Reduce rendering work for distant living entities.");
        entityRenderDistance = configuration.getInt("entityRenderDistance", "rendering", 96, 32, 256,
                "Maximum distance in blocks for living-entity rendering when the optimization is active.");

        optimizeChunkUpdates = configuration.getBoolean("optimizeChunkUpdates", "chunks", true,
                "Enable conservative chunk-work scheduling.");
        chunkUpdateRadius = configuration.getInt("chunkUpdateRadius", "chunks", 12, 2, 32,
                "Chunk radius considered useful for optional client chunk work.");
        maxChunkUpdatesPerTick = configuration.getInt("maxChunkUpdatesPerTick", "chunks", 2, 1, 16,
                "Maximum optional chunk-work slots reserved per client tick.");

        entityUpdateDistance = configuration.getInt("entityUpdateDistance", "entities", 64, 16, 128,
                "Distance in blocks beyond which experimental living-entity updates may be throttled.");
        maxParticlesPerTick = configuration.getInt("maxParticlesPerTick", "particles", 80, 16, 512,
                "Maximum optional particle-processing budget per client tick.");

        if (configuration.hasChanged()) configuration.save();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
}
