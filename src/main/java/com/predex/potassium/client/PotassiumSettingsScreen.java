package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import com.predex.potassium.optimization.benchmark.BenchmarkMonitor;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

/**
 * Potassium's main performance control panel.
 *
 * Controls are real Potassium settings; category buttons no longer perform
 * hidden group toggles. Each page exposes the actual setting it changes.
 */
public final class PotassiumSettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private int page;

    public PotassiumSettingsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        int center = width / 2;
        int left = center - 155;
        int right = center + 5;
        int y = page == 2 ? 70 : 52;

        if (page == 0) {
            addButton(1, left, y, 310, masterText());
            addButton(2, left, y + 24, 310, profileText());

            addButton(10, left, y + 58, 150, "Fast Render: " + onOff(PotassiumConfig.fastRender));
            addButton(11, right, y + 58, 150, "Fast Math: " + onOff(PotassiumConfig.fastMath));
            addButton(12, left, y + 82, 150, "Smart Animations: " + onOff(PotassiumConfig.smartAnimations));
            addButton(13, right, y + 82, 150, "Block Face Culling: " + onOff(PotassiumConfig.blockFaceCulling));
            addButton(14, left, y + 106, 150, "Entity Culling: " + onOff(PotassiumConfig.entityOcclusionCulling));
            addButton(15, right, y + 106, 150, "Render Regions: " + onOff(PotassiumConfig.renderRegions));

            addButton(16, left, y + 140, 150, "Chunk Optimization: " + onOff(PotassiumConfig.optimizeChunkUpdates));
            addButton(17, right, y + 140, 150, "Lazy Chunk Loading: " + onOff(PotassiumConfig.lazyChunkLoading));
            addButton(18, left, y + 164, 150, "Dynamic Chunk Updates: " + onOff(PotassiumConfig.dynamicChunkUpdates));
            addButton(19, right, y + 164, 150, "Adaptive Performance: " + onOff(PotassiumConfig.adaptivePerformance));
        } else if (page == 1) {
            addButton(20, left, y, 150, "Entity Rendering: " + onOff(PotassiumConfig.optimizeEntityRendering));
            addButton(21, right, y, 150, "Entity Updates: " + onOff(PotassiumConfig.reduceEntityUpdates));
            addButton(22, left, y + 24, 150, "Particles: " + onOff(PotassiumConfig.reduceParticles));
            addButton(23, right, y + 24, 150, "Low Memory Mode: " + onOff(PotassiumConfig.lowMemoryMode));

            addButton(24, left, y + 58, 150, "Entity Distance: " + PotassiumConfig.entityRenderDistance);
            addButton(25, right, y + 58, 150, "Entity Update Distance: " + PotassiumConfig.entityUpdateDistance);
            addButton(26, left, y + 82, 150, "Particle Budget: " + PotassiumConfig.maxParticlesPerTick);
            addButton(27, right, y + 82, 150, "Chunk Radius: " + PotassiumConfig.chunkUpdateRadius);
            addButton(28, left, y + 106, 150, "Chunk Budget: " + PotassiumConfig.maxChunkUpdatesPerTick);
            addButton(29, right, y + 106, 150, "CPU Budget: " + PotassiumConfig.cpuBudgetMillis + "ms");
            addButton(30, left, y + 140, 150, "Memory Threshold: " + PotassiumConfig.memoryPressureThreshold + "%");
            addButton(31, right, y + 140, 150, "Core Renderer Hooks: " + onOff(PotassiumConfig.rendererCoreHooks));

            addButton(32, left, y + 174, 150, "Empty Draw Skip: " + onOff(PotassiumConfig.skipEmptyDrawCalls));
            addButton(33, right, y + 174, 150, "Smooth World: " + onOff(PotassiumConfig.smoothWorld));
        } else {
            addButton(40, left, y, 150, "Potassium Video Settings");
            addButton(41, right, y, 150, "Reset Potassium Defaults");

            addButton(42, left, y + 34, 150, "FPS Smoothing: " + onOff(PotassiumConfig.smoothFps));
            addButton(43, right, y + 34, 150, "Renderer Hooks: " + onOff(PotassiumConfig.rendererCoreHooks));
            addButton(44, left, y + 58, 150, "Master Optimization: " + onOff(PotassiumConfig.enabled));
            addButton(45, right, y + 58, 150, "Profile: " + PerformanceProfileManager.getActiveProfileName());
            addButton(46, left, y + 92, 150, "Render Sections: " + onOff(PotassiumConfig.renderSections));
            addButton(47, right, y + 92, 150, "Mesh Uploads: " + PotassiumConfig.maxMeshUploadsPerFrame);
            addButton(48, left, y + 116, 150, "Mesh Prep Workers: " + onOff(PotassiumConfig.customMeshPreparation));
            addButton(49, right, y + 116, 150, "Occlusion Budget: " + PotassiumConfig.maxEntityOcclusionTestsPerFrame);
            addButton(50, left, y + 140, 150, "Mesh Upload Pipeline: " + onOff(PotassiumConfig.meshUploadPipeline));

            drawCenteredString(fontRendererObj,
                    "Advanced controls are conservative and fail open.",
                    center, y + 100, 0xAAAAAA);
            drawCenteredString(fontRendererObj,
                    "Disabling optimization never disables frame-time monitoring.",
                    center, y + 116, 0xAAAAAA);
        }

        addButton(90, center - 100, height - 28, 95, "< Previous");
        addButton(91, center + 5, height - 28, 95, "Next >");
        addButton(99, center - 100, height - 52, 200, "Done");
    }

    private void addButton(int id, int x, int y, int width, String text) {
        buttonList.add(new GuiButton(id, x, y, width, 20, text));
    }

    private String masterText() {
        return "Optimization: " + onOff(PotassiumConfig.enabled) + "  |  Frame monitor: ON";
    }

    private String profileText() {
        return "Profile: " + PerformanceProfileManager.getActiveProfileName();
    }

    private String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    private String benchmarkState() {
        double fps = BenchmarkMonitor.getMeasuredFps();
        return fps <= 0.0D ? "READY" : String.format(java.util.Locale.ROOT, "%.0f FPS", fps);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                PotassiumConfig.enabled = !PotassiumConfig.enabled;
                save();
                break;
            case 2:
                cycleProfile();
                break;
            case 10:
                PotassiumConfig.fastRender = !PotassiumConfig.fastRender;
                break;
            case 11:
                PotassiumConfig.fastMath = !PotassiumConfig.fastMath;
                break;
            case 12:
                PotassiumConfig.smartAnimations = !PotassiumConfig.smartAnimations;
                break;
            case 13:
                PotassiumConfig.blockFaceCulling = !PotassiumConfig.blockFaceCulling;
                break;
            case 14:
                PotassiumConfig.entityOcclusionCulling = !PotassiumConfig.entityOcclusionCulling;
                break;
            case 15:
                PotassiumConfig.renderRegions = !PotassiumConfig.renderRegions;
                break;
            case 16:
                PotassiumConfig.optimizeChunkUpdates = !PotassiumConfig.optimizeChunkUpdates;
                break;
            case 17:
                PotassiumConfig.lazyChunkLoading = !PotassiumConfig.lazyChunkLoading;
                break;
            case 18:
                PotassiumConfig.dynamicChunkUpdates = !PotassiumConfig.dynamicChunkUpdates;
                break;
            case 19:
                PotassiumConfig.adaptivePerformance = !PotassiumConfig.adaptivePerformance;
                break;
            case 20:
                PotassiumConfig.optimizeEntityRendering = !PotassiumConfig.optimizeEntityRendering;
                break;
            case 21:
                PotassiumConfig.reduceEntityUpdates = !PotassiumConfig.reduceEntityUpdates;
                break;
            case 22:
                PotassiumConfig.reduceParticles = !PotassiumConfig.reduceParticles;
                break;
            case 23:
                PotassiumConfig.lowMemoryMode = !PotassiumConfig.lowMemoryMode;
                break;
            case 24:
                PotassiumConfig.entityRenderDistance = cycle(PotassiumConfig.entityRenderDistance, 32, 256, 32);
                break;
            case 25:
                PotassiumConfig.entityUpdateDistance = cycle(PotassiumConfig.entityUpdateDistance, 16, 128, 16);
                break;
            case 26:
                PotassiumConfig.maxParticlesPerTick = cycle(PotassiumConfig.maxParticlesPerTick, 16, 512, 16);
                break;
            case 27:
                PotassiumConfig.chunkUpdateRadius = cycle(PotassiumConfig.chunkUpdateRadius, 2, 32, 2);
                break;
            case 28:
                PotassiumConfig.maxChunkUpdatesPerTick = cycle(PotassiumConfig.maxChunkUpdatesPerTick, 1, 16, 1);
                break;
            case 29:
                PotassiumConfig.cpuBudgetMillis = cycle(PotassiumConfig.cpuBudgetMillis, 20, 50, 5);
                break;
            case 30:
                PotassiumConfig.memoryPressureThreshold = cycle(PotassiumConfig.memoryPressureThreshold, 60, 95, 5);
                break;
            case 31:
                PotassiumConfig.rendererCoreHooks = !PotassiumConfig.rendererCoreHooks;
                break;
            case 32:
                PotassiumConfig.skipEmptyDrawCalls = !PotassiumConfig.skipEmptyDrawCalls;
                break;
            case 33:
                PotassiumConfig.smoothWorld = !PotassiumConfig.smoothWorld;
                break;
            case 40:
                Minecraft.getMinecraft().displayGuiScreen(new PotassiumVideoSettingsScreen(this));
                return;
            case 41:
                resetDefaults();
                break;
            case 42:
                PotassiumConfig.smoothFps = !PotassiumConfig.smoothFps;
                break;
            case 43:
                PotassiumConfig.rendererCoreHooks = !PotassiumConfig.rendererCoreHooks;
                break;
            case 44:
                PotassiumConfig.enabled = !PotassiumConfig.enabled;
                break;
            case 45:
                cycleProfile();
                break;
            case 46:
                PotassiumConfig.renderSections = !PotassiumConfig.renderSections;
                break;
            case 47:
                PotassiumConfig.maxMeshUploadsPerFrame =
                        cycle(PotassiumConfig.maxMeshUploadsPerFrame, 1, 8, 1);
                break;
            case 48:
                PotassiumConfig.customMeshPreparation = !PotassiumConfig.customMeshPreparation;
                break;
            case 49:
                PotassiumConfig.maxEntityOcclusionTestsPerFrame =
                        cycle(PotassiumConfig.maxEntityOcclusionTestsPerFrame, 8, 256, 8);
                break;
            case 50:
                PotassiumConfig.meshUploadPipeline = !PotassiumConfig.meshUploadPipeline;
                break;
            case 51:
                FrameTimeMonitor.reset();
                PerformanceTelemetry.reset();
                BenchmarkMonitor.reset();
                break;
            case 52:
                BenchmarkMonitor.reset();
                FrameTimeMonitor.reset();
                PerformanceTelemetry.reset();
                break;
            case 90:
                page = Math.max(0, page - 1);
                break;
            case 91:
                page = Math.min(2, page + 1);
                break;
            case 99:
                Minecraft.getMinecraft().displayGuiScreen(parent);
                return;
            default:
                return;
        }

        save();
        initGui();
    }

    private void cycleProfile() {
        if ("ULTRA_LOW".equals(PerformanceProfileManager.getActiveProfileName())) {
            PotassiumConfig.performanceProfile = "LOW_END";
        } else if ("LOW_END".equals(PerformanceProfileManager.getActiveProfileName())) {
            PotassiumConfig.performanceProfile = "BALANCED";
        } else {
            PotassiumConfig.performanceProfile = "ULTRA_LOW";
        }

        PerformanceProfileManager.applyConfiguredProfile();
        save();
    }

    private int cycle(int value, int min, int max, int step) {
        int next = value + step;
        return next > max ? min : next;
    }

    private void resetDefaults() {
        PotassiumConfig.enabled = true;
        PotassiumConfig.lowMemoryMode = true;
        PotassiumConfig.adaptivePerformance = true;
        PotassiumConfig.performanceProfile = "ULTRA_LOW";
        PerformanceProfileManager.applyConfiguredProfile();

        PotassiumConfig.reduceParticles = true;
        PotassiumConfig.reduceEntityUpdates = false;
        PotassiumConfig.optimizeEntityRendering = true;
        PotassiumConfig.optimizeChunkUpdates = true;
        PotassiumConfig.reduceEntityUpdates = false;
        PotassiumConfig.smoothFps = true;
        PotassiumConfig.smoothWorld = true;
        PotassiumConfig.fastRender = true;
        PotassiumConfig.fastMath = true;
        PotassiumConfig.dynamicChunkUpdates = false;
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
        PotassiumConfig.maxMeshUploadsPerFrame = 2;
        PotassiumConfig.maxEntityOcclusionTestsPerFrame = 64;
    }

    private void save() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj,
                "Potassium Settings",
                width / 2,
                15,
                0xFFFFFF);

        drawCenteredString(fontRendererObj,
                "Page " + (page + 1) + "/3  |  Performance controls only",
                width / 2,
                30,
                0xAAAAAA);

        if (page == 2) {
            drawCenteredString(fontRendererObj,
                    String.format(java.util.Locale.ROOT,
                            "FPS %.0f  |  1%% low %.0f  |  0.1%% low %.0f  |  frame %.2f ms",
                            BenchmarkMonitor.getMeasuredFps(),
                            FrameTimeMonitor.getOnePercentLowFps(),
                            FrameTimeMonitor.getZeroPointOnePercentLowFps(),
                            FrameTimeMonitor.getAverageMs()),
                    width / 2, 40, 0xFFFFFF);
            drawCenteredString(fontRendererObj,
                    String.format(java.util.Locale.ROOT,
                            "Variance %.2f  |  Adaptive %d%%  |  Samples %d",
                            FrameTimeMonitor.getVarianceMs(),
                            com.predex.potassium.optimization.adaptive.AdaptivePerformanceController.getQualityPercent(),
                            FrameTimeMonitor.getSampleCount()),
                    width / 2, 52, 0xAAAAAA);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
