package com.predex.potassium.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public final class PotassiumConfig {
    private static Configuration configuration;

    public static boolean enabled = true;
    public static boolean lowMemoryMode = true;
    public static boolean adaptivePerformance = true;
    public static String performanceProfile = "PERFORMANCE";
    public static int memoryPressureThreshold = 85;
    public static int cpuBudgetMillis = 45;

    public static boolean reduceParticles = true;
    public static boolean reduceEntityUpdates = false;
    public static boolean optimizeEntityRendering = true;
    public static int entityRenderDistance = 96;
    public static boolean optimizeChunkUpdates = true;
    public static int chunkUpdateRadius = 12;
    public static int maxChunkUpdatesPerTick = 2;
    public static boolean mobileChunkStreaming = true;
    public static int mobileChunkLoadBudget = 2;
    public static int movementPredictionChunks = 2;
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
    public static boolean renderSections = true;
    public static boolean meshUploadPipeline = true;
    public static boolean customMeshPreparation = true;
    public static int maxMeshUploadsPerFrame = 2;
    public static int maxEntityOcclusionTestsPerFrame = 64;

    // Lightweight quality-of-life features. These remain independent from
    // the master optimization switch and are intentionally low-cost.
    public static boolean qolHud = true;
    public static boolean qolShowFps = true;
    public static boolean qolShowLowFps = true;
    public static boolean qolShowFrameTime = false;
    public static boolean qolShowCoordinates = false;
    public static boolean qolShowDirection = false;
    public static boolean qolShowBiome = false;
    public static boolean qolShowMemory = false;
    public static boolean qolShowSessionTime = false;
    public static int qolHudScale = 100;

    // Client-only cosmetic companion. This never affects the server/world state.
    public static boolean miniPetEnabled = false;
    public static int miniPetScale = 42;
    public static String miniPetType = "predex";

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
                "PERFORMANCE", "Potassium performance profile. HIGH=light, MEDIUM=balanced, LOW=lower optimization, PERFORMANCE=maximum optimization.");
        if (!"HIGH".equals(performanceProfile)
                && !"MEDIUM".equals(performanceProfile)
                && !"LOW".equals(performanceProfile)
                && !"PERFORMANCE".equals(performanceProfile)) {
            performanceProfile = "PERFORMANCE";
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

        mobileChunkStreaming = configuration.getBoolean("mobileChunkStreaming", "chunks", true,
                "Use mobile-first chunk streaming with directional admission and bounded load budgets.");

        mobileChunkLoadBudget = configuration.getInt("mobileChunkLoadBudget", "chunks", 2, 1, 4,
                "Maximum chunk rebuilds admitted per client tick by the mobile streaming governor.");

        movementPredictionChunks = configuration.getInt("movementPredictionChunks", "chunks", 2, 1, 3,
                "How many chunks ahead Potassium may predict from player movement.");

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

        qolHud = configuration.getBoolean("qolHud", "qol", true,
                "Show Potassium's lightweight in-game information HUD.");
        qolShowFps = configuration.getBoolean("qolShowFps", "qol", true,
                "Show current FPS in the Potassium HUD.");
        qolShowLowFps = configuration.getBoolean("qolShowLowFps", "qol", true,
                "Show 1% low and 0.1% low FPS in the Potassium HUD.");
        qolShowFrameTime = configuration.getBoolean("qolShowFrameTime", "qol", false,
                "Show average frame time in milliseconds in the Potassium HUD.");
        qolShowCoordinates = configuration.getBoolean("qolShowCoordinates", "qol", false,
                "Show player coordinates in the Potassium HUD.");
        qolShowDirection = configuration.getBoolean("qolShowDirection", "qol", false,
                "Show the player's facing direction in the Potassium HUD.");
        qolShowBiome = configuration.getBoolean("qolShowBiome", "qol", false,
                "Show the current biome name in the Potassium HUD.");
        qolShowMemory = configuration.getBoolean("qolShowMemory", "qol", false,
                "Show current Java heap usage in the Potassium HUD.");
        qolShowSessionTime = configuration.getBoolean("qolShowSessionTime", "qol", false,
                "Show elapsed client session time in the Potassium HUD.");
        qolHudScale = configuration.getInt("qolHudScale", "qol", 100, 75, 150,
                "Scale percentage for the Potassium HUD.");

        miniPetEnabled = configuration.getBoolean("miniPetEnabled", "qol", false,
                "Show Potassium's client-only mini pet companion.");
        miniPetScale = configuration.getInt("miniPetScale", "qol", 42, 25, 75,
                "Mini pet render scale percentage.");
        miniPetType = configuration.getString("miniPetType", "qol", "predex",
                "Client-only mini pet type. 30 cosmetic pet choices are available.");

        if (configuration.hasChanged()) configuration.save();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }
}
