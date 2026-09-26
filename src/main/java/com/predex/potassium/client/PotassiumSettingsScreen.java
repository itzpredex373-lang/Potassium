package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.benchmark.BenchmarkMonitor;
import com.predex.potassium.optimization.benchmark.FrameTimeMonitor;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Potassium settings.
 *
 * Every setting has a short mouse-hover explanation so the user can see what
 * it changes before enabling it. Performance profiles are all selectable.
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
        int y = 38;

        if (page == 0) {
            addButton(1, left, y, 310, masterText());

            addButton(3, left, y + 24, 74, "High");
            addButton(4, left + 79, y + 24, 74, "Mid");
            addButton(5, left + 158, y + 24, 74, "Low");
            addButton(6, left + 237, y + 24, 73, "Performance");

            addButton(10, left, y + 52, 150, "Fast Render: " + onOff(PotassiumConfig.fastRender));
            addButton(11, right, y + 52, 150, "Fast Math: " + onOff(PotassiumConfig.fastMath));
            addButton(12, left, y + 74, 150, "Smart Animations: " + onOff(PotassiumConfig.smartAnimations));
            addButton(13, right, y + 74, 150, "Block Face Culling: " + onOff(PotassiumConfig.blockFaceCulling));
            addButton(14, left, y + 96, 150, "Entity Culling: " + onOff(PotassiumConfig.entityOcclusionCulling));
            addButton(15, right, y + 96, 150, "Render Regions: " + onOff(PotassiumConfig.renderRegions));
            addButton(16, left, y + 118, 150, "Chunk Optimization: " + onOff(PotassiumConfig.optimizeChunkUpdates));
            addButton(17, right, y + 118, 150, "Lazy Chunk Loading: " + onOff(PotassiumConfig.lazyChunkLoading));
            addButton(18, left, y + 140, 150, "Dynamic Chunk Updates: " + onOff(PotassiumConfig.dynamicChunkUpdates));
            addButton(19, right, y + 140, 150, "Adaptive Performance: " + onOff(PotassiumConfig.adaptivePerformance));

            drawCenteredString(fontRendererObj,
                    "Profile: " + PerformanceProfileManager.getActiveProfileName()
                            + "  |  High = strong  |  Mid = balanced  |  Low = light + QoL  |  Performance = ultra",
                    center, y + 164, 0xAAAAAA);
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
            addButton(31, right, y + 110, 150, "Renderer Hooks: " + onOff(PotassiumConfig.rendererCoreHooks));
            addButton(32, left, y + 132, 150, "Empty Draw Skip: " + onOff(PotassiumConfig.skipEmptyDrawCalls));
            addButton(33, right, y + 132, 150, "Smooth World: " + onOff(PotassiumConfig.smoothWorld));
            addButton(34, left, y + 154, 150, "Chunk Streaming: " + onOff(PotassiumConfig.mobileChunkStreaming));
            addButton(35, right, y + 154, 150, "Movement Prediction: " + PotassiumConfig.movementPredictionChunks);
        } else if (page == 2) {
            addButton(60, left, y, 150, "QoL HUD: " + onOff(PotassiumConfig.qolHud));
            addButton(61, right, y, 150, "HUD Scale: " + PotassiumConfig.qolHudScale + "%");
            addButton(62, left, y + 22, 150, "FPS: ON");
            addButton(63, right, y + 22, 150, "1% / 0.1% Low: OFF");
            addButton(64, left, y + 44, 150, "Frame Time: " + onOff(PotassiumConfig.qolShowFrameTime));
            addButton(65, right, y + 44, 150, "Coordinates: " + onOff(PotassiumConfig.qolShowCoordinates));
            addButton(66, left, y + 66, 150, "Direction: " + onOff(PotassiumConfig.qolShowDirection));
            addButton(67, right, y + 66, 150, "Biome: " + onOff(PotassiumConfig.qolShowBiome));
            addButton(68, left, y + 88, 150, "Memory: " + onOff(PotassiumConfig.qolShowMemory));
            addButton(69, right, y + 88, 150, "Session Timer: " + onOff(PotassiumConfig.qolShowSessionTime));
            addButton(70, left, y + 110, 150, "Mini Pet: " + onOff(PotassiumConfig.miniPetEnabled));
            addButton(71, right, y + 110, 150, "Pet Scale: " + PotassiumConfig.miniPetScale + "%");
            addButton(72, left, y + 132, 310, "Pet: " + petName(PotassiumConfig.miniPetType));

            if (!PerformanceProfileManager.isQoLAllowed()) {
                for (Object obj : buttonList) {
                    if (!(obj instanceof GuiButton)) continue;
                    GuiButton b = (GuiButton) obj;
                    if (b.id >= 60 && b.id <= 72 && b.id != 62 && b.id != 63) b.enabled = false;
                }
            }
            if (!PerformanceProfileManager.isPetAllowed()) {
                for (Object obj : buttonList) {
                    if (!(obj instanceof GuiButton)) continue;
                    GuiButton b = (GuiButton) obj;
                    if (b.id >= 70 && b.id <= 72) b.enabled = false;
                }
            }

            drawCenteredString(fontRendererObj,
                    "FPS/Ping HUD stays available without F3. Extra QoL and the pet are profile-gated.",
                    center, y + 156, 0xAAAAAA);
        } else {
            addButton(40, left, y, 150, "Video Settings");
            addButton(41, right, y, 150, "Reset Potassium Defaults");
            addButton(42, left, y + 22, 150, "FPS Smoothing: " + onOff(PotassiumConfig.smoothFps));
            addButton(43, right, y + 22, 150, "Renderer Hooks: " + onOff(PotassiumConfig.rendererCoreHooks));
            addButton(44, left, y + 44, 150, "Master Optimization: " + onOff(PotassiumConfig.enabled));
            addButton(45, right, y + 44, 150, "Profile: " + PerformanceProfileManager.getActiveProfileName());
            addButton(46, left, y + 66, 150, "Render Sections: " + onOff(PotassiumConfig.renderSections));
            addButton(47, right, y + 66, 150, "Mesh Uploads: " + PotassiumConfig.maxMeshUploadsPerFrame);
            addButton(48, left, y + 88, 150, "Mesh Prep: " + onOff(PotassiumConfig.customMeshPreparation));
            addButton(49, right, y + 88, 150, "Occlusion Budget: " + PotassiumConfig.maxEntityOcclusionTestsPerFrame);
            addButton(50, left, y + 110, 150, "Mesh Pipeline: " + onOff(PotassiumConfig.meshUploadPipeline));
            addButton(51, right, y + 110, 150, "Reset Metrics");
            drawCenteredString(fontRendererObj,
                    "Hover any button to see what it changes.",
                    center, y + 136, 0xAAAAAA);
        }

        addButton(90, center - 155, height - 26, 95, "< Previous");
        addButton(91, center + 60, height - 26, 95, "Next >");
        addButton(99, center - 50, height - 26, 100, "Done");
    }

    private void addButton(int id, int x, int y, int width, String text) {
        buttonList.add(new GuiButton(id, x, y, width, 20, text));
    }

    private String masterText() {
        return "Optimization: " + onOff(PotassiumConfig.enabled) + "  |  FPS/Ping monitor: ON";
    }

    private String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    private String petName(String type) {
        return PotassiumPetTypes.displayName(type);
    }

    private int cycle(int value, int min, int max, int step) {
        int next = value + step;
        return next > max ? min : next;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                PotassiumConfig.enabled = !PotassiumConfig.enabled;
                break;
            case 3:
                selectProfile("HIGH");
                break;
            case 4:
                selectProfile("MEDIUM");
                break;
            case 5:
                selectProfile("LOW");
                break;
            case 6:
                selectProfile("PERFORMANCE");
                break;
            case 10: PotassiumConfig.fastRender = !PotassiumConfig.fastRender; break;
            case 11: PotassiumConfig.fastMath = !PotassiumConfig.fastMath; break;
            case 12: PotassiumConfig.smartAnimations = !PotassiumConfig.smartAnimations; break;
            case 13: PotassiumConfig.blockFaceCulling = !PotassiumConfig.blockFaceCulling; break;
            case 14: PotassiumConfig.entityOcclusionCulling = !PotassiumConfig.entityOcclusionCulling; break;
            case 15: PotassiumConfig.renderRegions = !PotassiumConfig.renderRegions; break;
            case 16: PotassiumConfig.optimizeChunkUpdates = !PotassiumConfig.optimizeChunkUpdates; break;
            case 17: PotassiumConfig.lazyChunkLoading = !PotassiumConfig.lazyChunkLoading; break;
            case 18: PotassiumConfig.dynamicChunkUpdates = !PotassiumConfig.dynamicChunkUpdates; break;
            case 19: PotassiumConfig.adaptivePerformance = !PotassiumConfig.adaptivePerformance; break;
            case 20: PotassiumConfig.optimizeEntityRendering = !PotassiumConfig.optimizeEntityRendering; break;
            case 21: PotassiumConfig.reduceEntityUpdates = !PotassiumConfig.reduceEntityUpdates; break;
            case 22: PotassiumConfig.reduceParticles = !PotassiumConfig.reduceParticles; break;
            case 23: PotassiumConfig.lowMemoryMode = !PotassiumConfig.lowMemoryMode; break;
            case 24: PotassiumConfig.entityRenderDistance = cycle(PotassiumConfig.entityRenderDistance, 32, 256, 32); break;
            case 25: PotassiumConfig.entityUpdateDistance = cycle(PotassiumConfig.entityUpdateDistance, 16, 128, 16); break;
            case 26: PotassiumConfig.maxParticlesPerTick = cycle(PotassiumConfig.maxParticlesPerTick, 16, 512, 16); break;
            case 27: PotassiumConfig.chunkUpdateRadius = cycle(PotassiumConfig.chunkUpdateRadius, 2, 32, 2); break;
            case 28: PotassiumConfig.maxChunkUpdatesPerTick = cycle(PotassiumConfig.maxChunkUpdatesPerTick, 1, 16, 1); break;
            case 29: PotassiumConfig.cpuBudgetMillis = cycle(PotassiumConfig.cpuBudgetMillis, 20, 50, 5); break;
            case 30: PotassiumConfig.memoryPressureThreshold = cycle(PotassiumConfig.memoryPressureThreshold, 60, 95, 5); break;
            case 31: PotassiumConfig.rendererCoreHooks = !PotassiumConfig.rendererCoreHooks; break;
            case 32: PotassiumConfig.skipEmptyDrawCalls = !PotassiumConfig.skipEmptyDrawCalls; break;
            case 33: PotassiumConfig.smoothWorld = !PotassiumConfig.smoothWorld; break;
            case 34: PotassiumConfig.mobileChunkStreaming = !PotassiumConfig.mobileChunkStreaming; break;
            case 35: PotassiumConfig.movementPredictionChunks = cycle(PotassiumConfig.movementPredictionChunks, 1, 3, 1); break;
            case 40:
                Minecraft.getMinecraft().displayGuiScreen(new PotassiumVideoSettingsScreen(this));
                return;
            case 41:
                resetDefaults();
                break;
            case 42: PotassiumConfig.smoothFps = !PotassiumConfig.smoothFps; break;
            case 43: PotassiumConfig.rendererCoreHooks = !PotassiumConfig.rendererCoreHooks; break;
            case 44: PotassiumConfig.enabled = !PotassiumConfig.enabled; break;
            case 45: cycleProfile(); break;
            case 46: PotassiumConfig.renderSections = !PotassiumConfig.renderSections; break;
            case 47: PotassiumConfig.maxMeshUploadsPerFrame = cycle(PotassiumConfig.maxMeshUploadsPerFrame, 1, 8, 1); break;
            case 48: PotassiumConfig.customMeshPreparation = !PotassiumConfig.customMeshPreparation; break;
            case 49: PotassiumConfig.maxEntityOcclusionTestsPerFrame = cycle(PotassiumConfig.maxEntityOcclusionTestsPerFrame, 8, 256, 8); break;
            case 50: PotassiumConfig.meshUploadPipeline = !PotassiumConfig.meshUploadPipeline; break;
            case 51:
                FrameTimeMonitor.reset();
                PerformanceTelemetry.reset();
                BenchmarkMonitor.reset();
                break;
            case 60: PotassiumConfig.qolHud = !PotassiumConfig.qolHud; break;
            case 61: PotassiumConfig.qolHudScale = cycle(PotassiumConfig.qolHudScale, 75, 150, 25); break;
            case 62: break;
            case 63: break;
            case 64: PotassiumConfig.qolShowFrameTime = !PotassiumConfig.qolShowFrameTime; break;
            case 65: PotassiumConfig.qolShowCoordinates = !PotassiumConfig.qolShowCoordinates; break;
            case 66: PotassiumConfig.qolShowDirection = !PotassiumConfig.qolShowDirection; break;
            case 67: PotassiumConfig.qolShowBiome = !PotassiumConfig.qolShowBiome; break;
            case 68: PotassiumConfig.qolShowMemory = !PotassiumConfig.qolShowMemory; break;
            case 69: PotassiumConfig.qolShowSessionTime = !PotassiumConfig.qolShowSessionTime; break;
            case 70:
                if (PerformanceProfileManager.isPetAllowed()) PotassiumConfig.miniPetEnabled = !PotassiumConfig.miniPetEnabled;
                break;
            case 71:
                if (PerformanceProfileManager.isPetAllowed()) PotassiumConfig.miniPetScale = cycle(PotassiumConfig.miniPetScale, 25, 75, 8);
                break;
            case 72:
                if (PerformanceProfileManager.isPetAllowed()) cyclePetType();
                break;
            case 90: page = Math.max(0, page - 1); break;
            case 91: page = Math.min(3, page + 1); break;
            case 99:
                Minecraft.getMinecraft().displayGuiScreen(parent);
                return;
            default:
                return;
        }

        save();
        initGui();
    }

    private void selectProfile(String profile) {
        PotassiumConfig.performanceProfile = profile;
        PerformanceProfileManager.applyConfiguredProfile();

        if (!PerformanceProfileManager.isPetAllowed()) {
            PotassiumConfig.miniPetEnabled = false;
        }
        save();
    }

    private void cycleProfile() {
        String current = PerformanceProfileManager.getActiveProfileName();
        if ("HIGH".equals(current)) selectProfile("MEDIUM");
        else if ("MEDIUM".equals(current)) selectProfile("LOW");
        else if ("LOW".equals(current)) selectProfile("PERFORMANCE");
        else selectProfile("HIGH");
    }

    private void cyclePetType() {
        int index = PotassiumPetTypes.indexOf(PotassiumConfig.miniPetType);
        PotassiumConfig.miniPetType = PotassiumPetTypes.get((index + 1) % PotassiumPetTypes.TYPES.length);
    }

    private void resetDefaults() {
        PotassiumConfig.enabled = true;
        PotassiumConfig.adaptivePerformance = true;
        PotassiumConfig.performanceProfile = "MEDIUM";
        PerformanceProfileManager.applyConfiguredProfile();

        PotassiumConfig.smoothFps = true;
        PotassiumConfig.smoothWorld = true;
        PotassiumConfig.dynamicChunkUpdates = false;
        PotassiumConfig.reduceEntityUpdates = false;
        PotassiumConfig.qolHud = true;
        PotassiumConfig.qolShowFps = false;
        PotassiumConfig.qolShowLowFps = false;
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

    private void save() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj, "Potassium Settings",
                width / 2, 12, 0xFFFFFF);
        drawCenteredString(fontRendererObj,
                "Page " + (page + 1) + "/4",
                width / 2, 24, 0xAAAAAA);

        super.drawScreen(mouseX, mouseY, partialTicks);

        GuiButton hovered = null;
        for (Object obj : buttonList) {
            if (obj instanceof GuiButton) {
                GuiButton b = (GuiButton) obj;
                if (b.visible && mouseX >= b.xPosition && mouseY >= b.yPosition
                        && mouseX < b.xPosition + b.width
                        && mouseY < b.yPosition + b.height) {
                    hovered = b;
                    break;
                }
            }
        }

        if (hovered != null) {
            List<String> lines = descriptionFor(hovered.id);
            if (lines != null && !lines.isEmpty()) {
                drawHoveringText(lines, mouseX, mouseY);
            }
        }
    }

    private List<String> descriptionFor(int id) {
        String text;
        switch (id) {
            case 1: text = "Master switch. Turns Potassium optimizations on/off; FPS and monitoring stay available."; break;
            case 3: text = "High: strong optimization for systems that want a large FPS/frame-time reduction."; break;
            case 4: text = "Mid: balanced optimization with useful QoL features. Good general-purpose profile."; break;
            case 5: text = "Low: lighter optimization, more client QoL, and the mini pet. It intentionally gives up some FPS."; break;
            case 6: text = "Performance: ultra optimization. Prioritizes raw FPS, 1% lows and frame-time stability."; break;
            case 10: text = "Fast Render: uses Potassium fast paths where the hook is safe."; break;
            case 11: text = "Fast Math: caches Potassium hot-path math to reduce CPU work."; break;
            case 12: text = "Smart Animations: skips optional animation work when it is not useful."; break;
            case 13: text = "Block Face Culling: avoids drawing block faces that cannot be seen."; break;
            case 14: text = "Entity Culling: avoids rendering entities hidden behind blocks."; break;
            case 15: text = "Render Regions: groups render work so the renderer submits less fragmented work."; break;
            case 16: text = "Chunk Optimization: schedules chunk rebuild work instead of processing everything at once."; break;
            case 17: text = "Lazy Chunk Loading: spreads optional chunk preparation across ticks to reduce spikes."; break;
            case 18: text = "Dynamic Chunk Updates: allows extra chunk work when the player is stationary."; break;
            case 19: text = "Adaptive Performance: reacts to frame time, CPU and memory pressure automatically."; break;
            case 20: text = "Entity Rendering: reduces expensive entity rendering work at distance."; break;
            case 21: text = "Entity Updates: throttles selected living-entity updates. Experimental."; break;
            case 22: text = "Particles: reduces optional particle processing when enabled."; break;
            case 23: text = "Low Memory Mode: uses more conservative budgets when memory pressure rises."; break;
            case 24: text = "Entity Distance: maximum distance used by Potassium's entity render optimization."; break;
            case 25: text = "Entity Update Distance: distance used by optional entity-update throttling."; break;
            case 26: text = "Particle Budget: maximum optional particle processing per client tick."; break;
            case 27: text = "Chunk Radius: radius considered for optional chunk work."; break;
            case 28: text = "Chunk Budget: maximum optional chunk work slots per client tick."; break;
            case 29: text = "CPU Budget: target time budget for optional maintenance work."; break;
            case 30: text = "Memory Threshold: heap usage level where Potassium becomes more conservative."; break;
            case 31: text = "Renderer Hooks: enables Potassium's Forge 1.8.9 renderer bytecode hooks."; break;
            case 32: text = "Empty Draw Skip: avoids submitting empty Tessellator work."; break;
            case 33: text = "Smooth World: spreads optional world work to reduce large CPU bursts."; break;
            case 34: text = "Chunk Streaming: uses directional, bounded chunk scheduling for smoother movement."; break;
            case 35: text = "Movement Prediction: looks ahead a few chunks based on player motion."; break;
            case 40: text = "Open Minecraft-style Video Settings: graphics, render distance, clouds, particles, VSync, VBO and more."; break;
            case 41: text = "Restore Potassium's balanced default profile and QoL defaults."; break;
            case 42: text = "FPS Smoothing: adaptive workload control intended to reduce sudden frame-time changes."; break;
            case 43: text = "Renderer Hooks: enable/disable Potassium's core renderer hooks."; break;
            case 44: text = "Master Optimization: same global optimization switch as page 1."; break;
            case 45: text = "Cycle through High, Mid, Low and Performance profiles."; break;
            case 46: text = "Render Sections: tracks render sections for visibility and rebuild scheduling."; break;
            case 47: text = "Mesh Uploads: limits optional mesh uploads per render frame."; break;
            case 48: text = "Mesh Prep: enables bounded CPU-side mesh preparation."; break;
            case 49: text = "Occlusion Budget: limits entity occlusion tests per render frame."; break;
            case 50: text = "Mesh Pipeline: uses Potassium's bounded mesh upload admission queue."; break;
            case 51: text = "Reset Metrics: clears FPS, frame-time and telemetry samples."; break;
            case 60: text = "QoL HUD: controls the lightweight information overlay."; break;
            case 61: text = "HUD Scale: changes the size of the Potassium overlay."; break;
            case 62: text = "FPS: shows your current frames per second."; break;
            case 63: text = "1% / 0.1% Low FPS: intentionally hidden; server ping is shown separately."; break;
            case 64: text = "Frame Time: shows average frame time in milliseconds."; break;
            case 65: text = "Coordinates: shows XYZ without F3."; break;
            case 66: text = "Direction: shows the direction the player is facing."; break;
            case 67: text = "Biome: shows the current biome."; break;
            case 68: text = "Memory: shows current Java heap usage."; break;
            case 69: text = "Session Timer: shows elapsed client session time."; break;
            case 70: text = "Mini Pet: client-only cosmetic companion. Available only in Low profile."; break;
            case 71: text = "Pet Scale: changes the local pet's render size."; break;
            case 72: text = "Pet Selector: cycles through Potassium's original cosmetic pet choices."; break;
            case 90: text = "Go to the previous Potassium settings page."; break;
            case 91: text = "Go to the next Potassium settings page."; break;
            case 99: text = "Close Potassium settings."; break;
            default: return null;
        }
        return Collections.singletonList(text);
    }
}
