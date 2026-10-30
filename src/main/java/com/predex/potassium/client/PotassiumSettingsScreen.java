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
            addButton(19, right, y + 140, 150, "Adaptive Performance: " + onOff(PotassiumConfig.adaptivePerformance));

            drawCenteredString(fontRendererObj,
                    "Profile: " + PerformanceProfileManager.getActiveProfileName() + "  |  Adaptive CPU/memory workload control",
                    center, y + 164, 0xAAAAAA);
        } else if (page == 1) {
            addButton(23, right, y + 22, 150, "Low Memory Mode: " + onOff(PotassiumConfig.lowMemoryMode));
            addButton(29, right, y + 88, 150, "CPU Budget: " + PotassiumConfig.cpuBudgetMillis + "ms");
            addButton(30, left, y + 110, 150, "Memory Threshold: " + PotassiumConfig.memoryPressureThreshold + "%");
        } else if (page == 2) {
            addButton(60, left, y, 150, "QoL HUD: " + onOff(PotassiumConfig.qolHud));
            addButton(61, right, y, 150, "HUD Scale: " + PotassiumConfig.qolHudScale + "%");
            addButton(62, left, y + 22, 150, "FPS: ALWAYS ON");
            addButton(63, right, y + 22, 150, "Ping: ALWAYS ON");
            addButton(64, left, y + 44, 150, "Frame Time: " + onOff(PotassiumConfig.qolShowFrameTime));
            addButton(65, right, y + 44, 150, "Coordinates: " + onOff(PotassiumConfig.qolShowCoordinates));
            addButton(66, left, y + 66, 150, "Direction: " + onOff(PotassiumConfig.qolShowDirection));
            addButton(67, right, y + 66, 150, "Biome: " + onOff(PotassiumConfig.qolShowBiome));
            addButton(68, left, y + 88, 150, "Memory: " + onOff(PotassiumConfig.qolShowMemory));
            addButton(69, right, y + 88, 150, "Session Timer: " + onOff(PotassiumConfig.qolShowSessionTime));

            if (!PerformanceProfileManager.isQoLAllowed()) {
                for (Object obj : buttonList) {
                    if (!(obj instanceof GuiButton)) continue;
                    GuiButton b = (GuiButton) obj;
                    if (b.id >= 60 && b.id <= 72 && b.id != 62 && b.id != 63) b.enabled = false;
                }
            }
            drawCenteredString(fontRendererObj,
                    "FPS/Ping HUD stays available without F3. OptiFine owns zoom and visual controls.",
                    center, y + 180, 0xAAAAAA);
        } else {
            addButton(40, left, y, 150, "Video Settings");
            addButton(41, right, y, 150, "Reset Potassium Defaults");
            addButton(44, left, y + 44, 150, "Master Optimization: " + onOff(PotassiumConfig.enabled));
            addButton(51, right, y + 110, 150, "Reset Metrics");
            drawCenteredString(fontRendererObj,
                    "FPS: " + String.format(Locale.ROOT, "%.0f", BenchmarkMonitor.getMeasuredFps())
                            + " | Avg: " + String.format(Locale.ROOT, "%.2f", FrameTimeMonitor.getAverageMs()) + "ms",
                    center, y + 136, 0xAAAAAA);
            drawCenteredString(fontRendererObj,
                    "Hover any button to see what it changes.",
                    center, y + 148, 0x777777);
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
            case 19: PotassiumConfig.adaptivePerformance = !PotassiumConfig.adaptivePerformance; break;
            case 23: PotassiumConfig.lowMemoryMode = !PotassiumConfig.lowMemoryMode; break;
            case 29: PotassiumConfig.cpuBudgetMillis = cycle(PotassiumConfig.cpuBudgetMillis, 20, 50, 5); break;
            case 30: PotassiumConfig.memoryPressureThreshold = cycle(PotassiumConfig.memoryPressureThreshold, 60, 95, 5); break;
            case 40:
                Minecraft.getMinecraft().displayGuiScreen(new PotassiumVideoSettingsScreen(this));
                return;
            case 41:
                resetDefaults();
                break;
            case 44: PotassiumConfig.enabled = !PotassiumConfig.enabled; break;
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

        save();
    }

    private void cycleProfile() {
        String current = PerformanceProfileManager.getActiveProfileName();
        if ("HIGH".equals(current)) selectProfile("MEDIUM");
        else if ("MEDIUM".equals(current)) selectProfile("LOW");
        else if ("LOW".equals(current)) selectProfile("PERFORMANCE");
        else selectProfile("HIGH");
    }

    private void resetDefaults() {
        PotassiumConfig.enabled = true;
        PotassiumConfig.adaptivePerformance = true;
        PotassiumConfig.performanceProfile = "MEDIUM";
        PerformanceProfileManager.applyConfiguredProfile();
        PotassiumConfig.dynamicChunkUpdates = false;
        PotassiumConfig.reduceEntityUpdates = false;
        PotassiumConfig.qolHud = true;
        PotassiumConfig.qolShowFrameTime = false;
        PotassiumConfig.qolShowCoordinates = false;
        PotassiumConfig.qolShowDirection = false;
        PotassiumConfig.qolShowBiome = false;
        PotassiumConfig.qolShowMemory = false;
        PotassiumConfig.qolShowSessionTime = false;
        PotassiumConfig.qolHudScale = 100;
    }

    private void save() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj, "OptiDex Settings",
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
            case 5: text = "Low: lighter optimization with a few more client QoL options. It intentionally gives up some FPS."; break;
            case 6: text = "Performance: ultra optimization. Prioritizes raw FPS, 1% lows and frame-time stability."; break;
            case 19: text = "Adaptive Performance: reacts to frame time, CPU and memory pressure automatically."; break;
            case 23: text = "Low Memory Mode: uses more conservative budgets when memory pressure rises."; break;
            case 29: text = "CPU Budget: target time budget for optional maintenance work."; break;
            case 30: text = "Memory Threshold: heap usage level where Potassium becomes more conservative."; break;
            case 40: text = "Open Minecraft-style Video Settings: graphics, render distance, clouds, particles, VSync, VBO and more."; break;
            case 41: text = "Restore Potassium's balanced default profile and QoL defaults."; break;
            case 44: text = "Master Optimization: same global optimization switch as page 1."; break;
            case 51: text = "Reset Metrics: clears FPS, frame-time and telemetry samples."; break;
            case 60: text = "QoL HUD: controls the lightweight information overlay."; break;
            case 61: text = "HUD Scale: changes the size of the Potassium overlay."; break;
            case 62: text = "FPS: always shown in the Potassium HUD."; break;
            case 63: text = "Server Ping: always shown in the Potassium HUD when a server response is available."; break;
            case 64: text = "Frame Time: shows average frame time in milliseconds."; break;
            case 65: text = "Coordinates: shows XYZ without F3."; break;
            case 66: text = "Direction: shows the direction the player is facing."; break;
            case 67: text = "Biome: shows the current biome."; break;
            case 68: text = "Memory: shows current Java heap usage."; break;
            case 69: text = "Session Timer: shows elapsed client session time."; break;
            case 90: text = "Go to the previous Potassium settings page."; break;
            case 91: text = "Go to the next Potassium settings page."; break;
            case 99: text = "Close Potassium settings."; break;
            default: return null;
        }
        return Collections.singletonList(text);
    }
}
