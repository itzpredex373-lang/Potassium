package com.predex.potassium.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public final class PotassiumConfig {
    private static Configuration configuration;

    public static boolean enabled = true;
    public static boolean lowMemoryMode = true;
    public static boolean adaptivePerformance = true;
    public static String performanceProfile = "ULTRA_LOW";
    public static int memoryPressureThreshold = 85;
    public static int cpuBudgetMillis = 45;

    public static boolean reduceParticles = true;
    public static boolean reduceEntityUpdates = false;
    public static boolean optimizeEntityRendering = true;
    public static int entityRenderDistance = 96;
    public static boolean optimizeChunkUpdates = true;
    public static int chunkUpdateRadius = 12;
    public static int maxChunkUpdatesPerTick = 2;
    public static int entityUpdateDistance = 64;
    public static int maxParticlesPerTick = 80;

    // OptiFine-inspired performance controls. These are performance controls,
    // not visual/HD/shader features.
    public static boolean smoothFps = true;
    public static boolean smoothWorld = true;
    public static boolean fastRender = true;
    public static boolean fastMath = true;
    public static boolean dynamicChunkUpdates = false;
    public static boolean lazyChunkLoading = true;
    public static boolean renderRegions = true;
    public static boolean smartAnimations = true;
    public static boolean blockFaceCulling = true;
    public static boolean entityOcclusionCulling = true;
    public static boolean rendererCoreHooks = true;
    public static boolean skipEmptyDrawCalls = true;
    public static boolean customGpuRenderer = true;
    public static boolean customDrawSubmission = true;

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

        adaptivePerformance = configuration.getBoolean("adaptivePerformance", "performance", true,
                "Adapt optional optimization work to current memory pressure and client tick time.");

        performanceProfile = configuration.getString("performanceProfile", "performance",
                "ULTRA_LOW", "Potassium performance profile. Applied at startup.");
        if (!"BALANCED".equals(performanceProfile)
                && !"LOW_END".equals(performanceProfile)
                && !"ULTRA_LOW".equals(performanceProfile)) {
            performanceProfile = "ULTRA_LOW";
        }

        memoryPressureThreshold = configuration.getInt("memoryPressureThreshold", "performance",
                85, 60, 95,
                "Heap usage percentage at which Potassium starts reducing optional particle work.");

        cpuBudgetMillis = configuration.getInt("cpuBudgetMillis", "performance",
                45, 20, 50,
                "Client tick-time budget in milliseconds for optional maintenance work.");

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

        smoothFps = configuration.getBoolean("smoothFps", "performance", true,
                "Use adaptive workload control to reduce sudden frame-time spikes.");

        smoothWorld = configuration.getBoolean("smoothWorld", "performance", true,
                "Distribute optional single-player world work instead of doing large bursts.");

        fastRender = configuration.getBoolean("fastRender", "performance", true,
                "Enable Potassium fast-path rendering decisions where a safe hook is available.");

        fastMath = configuration.getBoolean("fastMath", "performance", true,
                "Use Potassium's cached trigonometry helpers for its own hot paths.");

        dynamicChunkUpdates = configuration.getBoolean("dynamicChunkUpdates", "chunks", false,
                "Allow extra chunk scheduling while the player is standing still.");

        lazyChunkLoading = configuration.getBoolean("lazyChunkLoading", "chunks", true,
                "Spread optional chunk preparation over multiple ticks to reduce spikes.");

        renderRegions = configuration.getBoolean("renderRegions", "rendering", true,
                "Enable region-aware render scheduling helpers.");

        smartAnimations = configuration.getBoolean("smartAnimations", "rendering", true,
                "Only admit optional animation work when its texture/region is visible.");

        blockFaceCulling = configuration.getBoolean("blockFaceCulling", "rendering", true,
                "Skip fully hidden opaque blocks during the baked-model render path.");

        entityOcclusionCulling = configuration.getBoolean("entityOcclusionCulling", "rendering", true,
                "Hide entities whose bounding-box sample points are fully behind opaque blocks.");

        rendererCoreHooks = configuration.getBoolean("rendererCoreHooks", "rendering", true,
                "Enable Potassium's Forge 1.8.9 bytecode renderer hooks.");

        skipEmptyDrawCalls = configuration.getBoolean("skipEmptyDrawCalls", "rendering", true,
                "Skip Tessellator submissions that contain zero vertices.");

        customGpuRenderer = configuration.getBoolean("customGpuRenderer", "rendering", true,
                "Use Potassium-owned VBO storage for compiled chunk meshes when the GPU supports it.");

        customDrawSubmission = configuration.getBoolean("customDrawSubmission", "rendering", true,
                "Submit ready chunk meshes through Potassium's custom VBO renderer; fall back to vanilla when incomplete.");

        renderSections = configuration.getBoolean("renderSections", "rendering", true,
                "Track chunk sections separately for visibility and rebuild scheduling.");

        meshUploadPipeline = configuration.getBoolean("meshUploadPipeline", "rendering", true,
                "Use a bounded client-thread mesh upload admission queue.");

        customMeshPreparation = configuration.getBoolean("customMeshPreparation", "rendering", true,
                "Enable the bounded CPU-side mesh preparation worker pool.");

        maxMeshUploadsPerFrame = configuration.getInt("maxMeshUploadsPerFrame", "rendering",
                2, 1, 8, "Maximum optional mesh upload tasks admitted per render frame.");

        maxEntityOcclusionTestsPerFrame = configuration.getInt("maxEntityOcclusionTestsPerFrame",
                "rendering", 64, 8, 256,
                "Maximum entity occlusion ray-test groups per render frame.");

        if (configuration.hasChanged()) configuration.save();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
}
