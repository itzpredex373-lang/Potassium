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
import java.util.ArrayList;
import java.util.List;

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
        // Keep the layout inside the smallest common 1.8.9 GUI sizes.
        int y = 40;

        if (page == 0) {
            addButton(1, left, y, 310, masterText());

            addButton(3, left, y + 22, 74, "High");
            addButton(4, left + 79, y + 22, 74, "Mid");
            addButton(5, left + 158, y + 22, 74, "Low");
            addButton(6, left + 237, y + 22, 73, "Performance");
            setProfileButtonEnabled(3, false);
            setProfileButtonEnabled(6, false);

            addButton(10, left, y + 44, 150, "Fast Render: " + onOff(PotassiumConfig.fastRender));
            addButton(11, right, y + 44, 150, "Fast Math: " + onOff(PotassiumConfig.fastMath));
            addButton(12, left, y + 66, 150, "Smart Animations: " + onOff(PotassiumConfig.smartAnimations));
            addButton(13, right, y + 66, 150, "Block Face Culling: " + onOff(PotassiumConfig.blockFaceCulling));
            addButton(14, left, y + 88, 150, "Entity Culling: " + onOff(PotassiumConfig.entityOcclusionCulling));
            addButton(15, right, y + 88, 150, "Render Regions: " + onOff(PotassiumConfig.renderRegions));

            addButton(16, left, y + 110, 150, "Chunk Optimization: " + onOff(PotassiumConfig.optimizeChunkUpdates));
            addButton(17, right, y + 110, 150, "Lazy Chunk Loading: " + onOff(PotassiumConfig.lazyChunkLoading));
            addButton(18, left, y + 132, 150, "Dynamic Chunk Updates: " + onOff(PotassiumConfig.dynamicChunkUpdates));
            addButton(19, right, y + 132, 150, "Adaptive Performance: " + onOff(PotassiumConfig.adaptivePerformance));
        } else if (page == 1) {
            addButton(20, left, y, 150, "Entity Rendering: " + onOff(PotassiumConfig.optimizeEntityRendering));
            addButton(21, right, y, 150, "Entity Updates: " + onOff(PotassiumConfig.reduceEntityUpdates));
            addButton(22, left, y + 22, 150, "Particles: " + onOff(PotassiumConfig.reduceParticles));
            addButton(23, right, y + 22, 150, "Low Memory Mode: " + onOff(PotassiumConfig.lowMemoryMode));

            addButton(24, left, y + 44, 150, "Entity Distance: " + PotassiumConfig.entityRenderDistance);
            addButton(25, right, y + 44, 150, "Entity Update Distance: " + PotassiumConfig.entityUpdateDistance);
            addButton(26, left, y + 66, 150, "Particle Budget: " + PotassiumConfig.maxParticlesPerTick);
            addButton(27, right, y + 66, 150, "Chunk Radius: " + PotassiumConfig.chunkUpdateRadius);
            addButton(28, left, y + 88, 150, "Chunk Budget: " + PotassiumConfig.maxChunkUpdatesPerTick);
            addButton(29, right, y + 88, 150, "CPU Budget: " + PotassiumConfig.cpuBudgetMillis + "ms");
            addButton(30, left, y + 110, 150, "Memory Threshold: " + PotassiumConfig.memoryPressureThreshold + "%");
            addButton(31, right, y + 110, 150, "Core Renderer Hooks: " + onOff(PotassiumConfig.rendererCoreHooks));

            addButton(32, left, y + 154, 150, "Empty Draw Skip: " + onOff(PotassiumConfig.skipEmptyDrawCalls));
            addButton(33, right, y + 154, 150, "Smooth World: " + onOff(PotassiumConfig.smoothWorld));
        } else if (page == 2) {
            addButton(60, left, y, 150, "QoL HUD: " + onOff(PotassiumConfig.qolHud));
            addButton(61, right, y, 150, "HUD Scale: " + PotassiumConfig.qolHudScale + "%");
            addButton(62, left, y + 22, 150, "FPS: " + onOff(PotassiumConfig.qolShowFps));
            addButton(63, right, y + 22, 150, "1% / 0.1% Low: " + onOff(PotassiumConfig.qolShowLowFps));
            addButton(64, left, y + 44, 150, "Frame Time: " + onOff(PotassiumConfig.qolShowFrameTime));
            addButton(65, right, y + 44, 150, "Coordinates: " + onOff(PotassiumConfig.qolShowCoordinates));
            addButton(66, left, y + 66, 150, "Direction: " + onOff(PotassiumConfig.qolShowDirection));
            addButton(67, right, y + 66, 150, "Biome: " + onOff(PotassiumConfig.qolShowBiome));
            addButton(68, left, y + 88, 150, "Memory: " + onOff(PotassiumConfig.qolShowMemory));
            addButton(69, right, y + 88, 150, "Session Timer: " + onOff(PotassiumConfig.qolShowSessionTime));
            addButton(70, left, y + 110, 150, "Mini Pet: " + onOff(PotassiumConfig.miniPetEnabled));
            addButton(71, right, y + 110, 150, "Pet Scale: " + PotassiumConfig.miniPetScale + "%");
            addButton(72, left, y + 132, 310, "Pet: " + petName(PotassiumConfig.miniPetType));
            drawCenteredString(fontRendererObj, "QoL is independent from Optimization.", center, y + 156, 0xAAAAAA);
        } else {
            addButton(40, left, y, 150, "Potassium Video Settings");
            addButton(41, right, y, 150, "Reset Potassium Defaults");

            addButton(42, left, y + 22, 150, "FPS Smoothing: " + onOff(PotassiumConfig.smoothFps));
            addButton(43, right, y + 22, 150, "Renderer Hooks: " + onOff(PotassiumConfig.rendererCoreHooks));
            addButton(44, left, y + 44, 150, "Master Optimization: " + onOff(PotassiumConfig.enabled));
            addButton(45, right, y + 44, 150, "Profile: " + PerformanceProfileManager.getActiveProfileName());
            addButton(46, left, y + 88, 150, "Render Sections: " + onOff(PotassiumConfig.renderSections));
            addButton(47, right, y + 88, 150, "Mesh Uploads: " + PotassiumConfig.maxMeshUploadsPerFrame);
            addButton(48, left, y + 110, 150, "Mesh Prep Workers: " + onOff(PotassiumConfig.customMeshPreparation));
            addButton(49, right, y + 110, 150, "Occlusion Budget: " + PotassiumConfig.maxEntityOcclusionTestsPerFrame);
            addButton(50, left, y + 110, 150, "Mesh Upload Pipeline: " + onOff(PotassiumConfig.meshUploadPipeline));

            drawCenteredString(fontRendererObj,
                    "Advanced controls are conservative and fail open.",
                    center, y + 100, 0xAAAAAA);
            drawCenteredString(fontRendererObj,
                    "Disabling optimization never disables frame-time monitoring.",
                    center, y + 110, 0xAAAAAA);
        }

        addButton(90, center - 155, height - 26, 95, "< Previous");
        addButton(91, center + 60, height - 26, 95, "Next >");
        addButton(99, center - 50, height - 26, 100, "Done");
        if (page == 2 && !PerformanceProfileManager.isQoLAllowed()) {
            for (Object obj : buttonList) {
                if (!(obj instanceof GuiButton)) continue;
                GuiButton b = (GuiButton) obj;
                if (b.id >= 60 && b.id <= 72) b.enabled = false;
            }
        }


    }

    private void addButton(int id, int x, int y, int width, String text) {
        buttonList.add(new GuiButton(id, x, y, width, 20, text));
    }

    private void setProfileButtonEnabled(int id, boolean enabled) {
        for (Object obj : buttonList) {
            if (!(obj instanceof GuiButton)) continue;
            GuiButton button = (GuiButton) obj;
            if (button.id == id) {
                button.enabled = enabled;
                return;
            }
        }
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
            case 3:
                return;
            case 4:
                selectProfile("MEDIUM");
                break;
            case 5:
                selectProfile("LOW");
                break;
            case 6:
                return;
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
            case 60:
                PotassiumConfig.qolHud = !PotassiumConfig.qolHud;
                break;
            case 61:
                PotassiumConfig.qolHudScale = cycle(PotassiumConfig.qolHudScale, 75, 150, 25);
                break;
            case 62:
                PotassiumConfig.qolShowFps = !PotassiumConfig.qolShowFps;
                break;
            case 63:
                PotassiumConfig.qolShowLowFps = !PotassiumConfig.qolShowLowFps;
                break;
            case 64:
                PotassiumConfig.qolShowFrameTime = !PotassiumConfig.qolShowFrameTime;
                break;
            case 65:
                PotassiumConfig.qolShowCoordinates = !PotassiumConfig.qolShowCoordinates;
                break;
            case 66:
                PotassiumConfig.qolShowDirection = !PotassiumConfig.qolShowDirection;
                break;
            case 67:
                PotassiumConfig.qolShowBiome = !PotassiumConfig.qolShowBiome;
                break;
            case 68:
                PotassiumConfig.qolShowMemory = !PotassiumConfig.qolShowMemory;
                break;
            case 69:
                PotassiumConfig.qolShowSessionTime = !PotassiumConfig.qolShowSessionTime;
                break;
            case 70:
                PotassiumConfig.miniPetEnabled = !PotassiumConfig.miniPetEnabled;
                break;
            case 71:
                PotassiumConfig.miniPetScale = cycle(PotassiumConfig.miniPetScale, 25, 75, 8);
                break;
            case 72:
                cyclePetType();
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
                page = Math.min(3, page + 1);
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
        String current = PerformanceProfileManager.getActiveProfileName();
        if ("MEDIUM".equals(current)) selectProfile("LOW");
        else selectProfile("MEDIUM");
    }

    private void selectProfile(String profile) {
        PotassiumConfig.performanceProfile = profile;
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
        PotassiumConfig.performanceProfile = "MEDIUM";
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
        PotassiumConfig.qolHud = true;
        PotassiumConfig.qolShowFps = true;
        PotassiumConfig.qolShowLowFps = true;
        PotassiumConfig.qolShowFrameTime = false;
        PotassiumConfig.qolShowCoordinates = false;
        PotassiumConfig.qolShowDirection = false;
        PotassiumConfig.qolShowBiome = false;
        PotassiumConfig.qolShowMemory = false;
        PotassiumConfig.qolShowSessionTime = false;
        PotassiumConfig.qolHudScale = 100;
        PotassiumConfig.miniPetEnabled = false;
        PotassiumConfig.miniPetScale = 42;
        PotassiumConfig.miniPetType = "predex";
    }

    private void cyclePetType() {
        String[] types = {
                "predex", "wolf", "dragon", "devil", "blaze",
                "slime", "endermite", "bat", "chicken", "rabbit", "ocelot"
        };
        int current = 0;
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(PotassiumConfig.miniPetType)) {
                current = i;
                break;
            }
        }
        PotassiumConfig.miniPetType = types[(current + 1) % types.length];
    }

    private String petName(String type) {
        if ("predex".equals(type)) return "Predex Pet";
        if ("dragon".equals(type)) return "Mini King Dragon";
        if ("devil".equals(type)) return "Mini Devil";
        if ("blaze".equals(type)) return "Blaze";
        if ("slime".equals(type)) return "Slime";
        if ("endermite".equals(type)) return "Endermite";
        if ("rabbit".equals(type)) return "Rabbit";
        if ("ocelot".equals(type)) return "Ocelot";
        return type.substring(0, 1).toUpperCase() + type.substring(1);
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
                "Page " + (page + 1) + "/4  |  Performance + Quality of Life",
                width / 2,
                30,
                0xAAAAAA);

        if (page == 0) {
            drawCenteredString(fontRendererObj,
                    "Active: " + PerformanceProfileManager.getActiveProfileName()
                            + "  |  QoL: " + (PerformanceProfileManager.isQoLAllowed() ? "ON" : "OFF"),
                    width / 2, 42, 0xFFFFFF);
        }

        if (page == 3) {
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
        drawSettingTooltip(mouseX, mouseY);
    }

    private void drawSettingTooltip(int mouseX, int mouseY) {
        for (Object obj : buttonList) {
            if (!(obj instanceof GuiButton)) continue;
            GuiButton button = (GuiButton) obj;
            if (mouseX < button.xPosition || mouseX > button.xPosition + button.width
                    || mouseY < button.yPosition || mouseY > button.yPosition + button.height) continue;

            String description = getSettingDescription(button.id);
            if (description == null) continue;
            List<String> lines = new ArrayList<String>();
            for (String line : description.split("\\n")) lines.add(line);
            drawHoveringText(lines, mouseX, mouseY);
            break;
        }
    }

    private String getSettingDescription(int id) {
        switch (id) {
            case 1: return "Master Optimization: enables Potassium performance optimizations. Monitoring stays active when OFF.";
            case 3: return "High: light optimization and higher budgets. QoL disabled.";
            case 4: return "Mid: balanced optimization. QoL available.";
            case 5: return "Low: stronger optimization for weaker hardware. QoL available.";
            case 6: return "Performance: strongest workload reduction. QoL disabled to minimize overhead.";
            case 10: return "Fast Render reduces safe rendering-path overhead.";
            case 11: return "Fast Math uses cached math helpers in Potassium hot paths.";
            case 12: return "Smart Animations avoids unnecessary optional animation work.";
            case 13: return "Block Face Culling skips fully hidden block faces.";
            case 14: return "Entity Culling skips entities fully hidden by opaque geometry.";
            case 15: return "Render Regions improves chunk render grouping and scheduling.";
            case 16: return "Chunk Optimization limits and prioritizes chunk rebuild work.";
            case 17: return "Lazy Chunk Loading spreads optional chunk preparation over ticks.";
            case 18: return "Dynamic Chunk Updates allows extra scheduling while standing still.";
            case 19: return "Adaptive Performance reacts to frame time, CPU, and memory pressure.";
            case 20: return "Entity Rendering reduces unnecessary distant-entity render work.";
            case 21: return "Entity Updates throttles distant living-entity updates. Experimental.";
            case 22: return "Particles limits optional particle processing.";
            case 23: return "Low Memory Mode uses more conservative memory-aware budgets.";
            case 24: return "Entity Distance controls Potassium's entity render optimization range.";
            case 25: return "Entity Update Distance controls the experimental entity-update range.";
            case 26: return "Particle Budget is the optional particle-processing budget per tick.";
            case 27: return "Chunk Radius controls the area considered for optional chunk work.";
            case 28: return "Chunk Budget limits optional chunk-work slots per tick.";
            case 29: return "CPU Budget is the time budget for optional maintenance work.";
            case 30: return "Memory Threshold controls when optional work is reduced.";
            case 31: return "Core Renderer Hooks enables Potassium Forge 1.8.9 bytecode hooks.";
            case 32: return "Empty Draw Skip avoids zero-vertex Tessellator submissions.";
            case 33: return "Smooth World spreads optional single-player world work.";
            case 60: return "QoL HUD: information HUD. Only Mid and Low profiles allow QoL.";
            case 61: return "HUD Scale changes QoL HUD size.";
            case 62: return "FPS shows current measured FPS.";
            case 63: return "1% / 0.1% Low shows low-FPS metrics.";
            case 64: return "Frame Time shows average frame time.";
            case 65: return "Coordinates shows player XYZ.";
            case 66: return "Direction shows facing direction.";
            case 67: return "Biome shows the current biome.";
            case 68: return "Memory shows Java heap usage.";
            case 69: return "Session Timer shows elapsed client session time.";
            case 70: return "Mini Pet toggles the client-only cosmetic pet.";
            case 71: return "Pet Scale changes mini pet render size.";
            case 72: return "Pet cycles through client-only pet choices.";
            case 40: return "Open Minecraft video options and Potassium video controls.";
            case 41: return "Reset Potassium settings to safe defaults.";
            case 42: return "FPS Smoothing adapts optional workloads to reduce frame-time spikes.";
            case 43: return "Renderer Hooks toggles Potassium bytecode renderer hooks.";
            case 44: return "Master Optimization toggles optimization only; monitoring stays active.";
            case 45: return "Shows the currently active performance profile.";
            case 46: return "Render Sections tracks chunk sections for visibility and rebuild scheduling.";
            case 47: return "Mesh Uploads limits optional mesh uploads per render frame.";
            case 48: return "Mesh Prep Workers enables bounded CPU-side mesh preparation.";
            case 49: return "Occlusion Budget limits entity occlusion tests per render frame.";
            case 50: return "Mesh Upload Pipeline enables the bounded mesh upload queue.";
            default: return null;
        }
    }
}
