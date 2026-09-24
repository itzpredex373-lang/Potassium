package com.predex.potassium.dev;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.benchmark.PotassiumDevDiagnostics;
import com.predex.potassium.optimization.profile.PerformanceProfile;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/** Temporary developer panel for Dev Temp Version 1. */
public final class PotassiumDevScreen extends GuiScreen {
    private static final int MASTER_ID = 1;
    private static final int PROFILE_ID = 2;
    private static final int RESET_ID = 98;
    private static final int CLOSE_ID = 99;

    private static final String[][] TOGGLES = new String[][] {
            {"Entity Culling", "optimizeEntityRendering"},
            {"Block Culling", "blockFaceCulling"},
            {"Renderer Hooks", "rendererCoreHooks"},
            {"Entity Occlusion", "entityOcclusionCulling"},
            {"Smart Animations", "smartAnimations"},
            {"Empty Draw Skip", "skipEmptyDrawCalls"},
            {"Particles", "reduceParticles"},
            {"Entity Updates", "reduceEntityUpdates"},
            {"Chunk Updates", "optimizeChunkUpdates"},
            {"Dynamic Chunks", "dynamicChunkUpdates"},
            {"Lazy Chunks", "lazyChunkLoading"},
            {"Render Regions", "renderRegions"},
            {"Adaptive Performance", "adaptivePerformance"},
            {"Low Memory Mode", "lowMemoryMode"},
            {"Smooth FPS", "smoothFps"},
            {"Smooth World", "smoothWorld"},
            {"Fast Render", "fastRender"},
            {"Fast Math", "fastMath"}
    };

    private GuiButton masterButton;
    private GuiButton profileButton;

    @Override
    public void initGui() {
        buttonList.clear();

        int panelLeft = width / 2 - 225;
        int columnWidth = 145;
        int gap = 8;
        int top = 54;

        profileButton = new GuiButton(PROFILE_ID, panelLeft, 30, columnWidth, 20, "");
        masterButton = new GuiButton(MASTER_ID, width / 2 + 5, 30, columnWidth, 20, "");
        buttonList.add(profileButton);
        buttonList.add(masterButton);

        for (int i = 0; i < TOGGLES.length; i++) {
            int column = i % 3;
            int row = i / 3;
            int x = panelLeft + column * (columnWidth + gap);
            int y = top + 22 + row * 22;
            buttonList.add(new GuiButton(10 + i, x, y, columnWidth, 20,
                    label(TOGGLES[i][0], getToggle(TOGGLES[i][1]))));
        }

        int footerY = top + 6 * 22 + 20;
        buttonList.add(new GuiButton(RESET_ID, width / 2 - 155, footerY + 54, 145, 20, "RESET"));
        buttonList.add(new GuiButton(CLOSE_ID, width / 2 + 10, footerY + 54, 145, 20, "CLOSE"));

        updateTopButtons();
    }

    private boolean getToggle(String key) {
        if ("reduceParticles".equals(key)) return PotassiumConfig.reduceParticles;
        if ("optimizeEntityRendering".equals(key)) return PotassiumConfig.optimizeEntityRendering;
        if ("reduceEntityUpdates".equals(key)) return PotassiumConfig.reduceEntityUpdates;
        if ("optimizeChunkUpdates".equals(key)) return PotassiumConfig.optimizeChunkUpdates;
        if ("rendererCoreHooks".equals(key)) return PotassiumConfig.rendererCoreHooks;
        if ("blockFaceCulling".equals(key)) return PotassiumConfig.blockFaceCulling;
        if ("entityOcclusionCulling".equals(key)) return PotassiumConfig.entityOcclusionCulling;
        if ("adaptivePerformance".equals(key)) return PotassiumConfig.adaptivePerformance;
        if ("lowMemoryMode".equals(key)) return PotassiumConfig.lowMemoryMode;
        if ("smoothFps".equals(key)) return PotassiumConfig.smoothFps;
        if ("smoothWorld".equals(key)) return PotassiumConfig.smoothWorld;
        if ("fastRender".equals(key)) return PotassiumConfig.fastRender;
        if ("fastMath".equals(key)) return PotassiumConfig.fastMath;
        if ("lazyChunkLoading".equals(key)) return PotassiumConfig.lazyChunkLoading;
        if ("renderRegions".equals(key)) return PotassiumConfig.renderRegions;
        if ("smartAnimations".equals(key)) return PotassiumConfig.smartAnimations;
        if ("skipEmptyDrawCalls".equals(key)) return PotassiumConfig.skipEmptyDrawCalls;
        if ("dynamicChunkUpdates".equals(key)) return PotassiumConfig.dynamicChunkUpdates;
        return false;
    }

    private void setToggle(String key, boolean value) {
        if ("reduceParticles".equals(key)) PotassiumConfig.reduceParticles = value;
        else if ("optimizeEntityRendering".equals(key)) PotassiumConfig.optimizeEntityRendering = value;
        else if ("reduceEntityUpdates".equals(key)) PotassiumConfig.reduceEntityUpdates = value;
        else if ("optimizeChunkUpdates".equals(key)) PotassiumConfig.optimizeChunkUpdates = value;
        else if ("rendererCoreHooks".equals(key)) PotassiumConfig.rendererCoreHooks = value;
        else if ("blockFaceCulling".equals(key)) PotassiumConfig.blockFaceCulling = value;
        else if ("entityOcclusionCulling".equals(key)) PotassiumConfig.entityOcclusionCulling = value;
        else if ("adaptivePerformance".equals(key)) PotassiumConfig.adaptivePerformance = value;
        else if ("lowMemoryMode".equals(key)) PotassiumConfig.lowMemoryMode = value;
        else if ("smoothFps".equals(key)) PotassiumConfig.smoothFps = value;
        else if ("smoothWorld".equals(key)) PotassiumConfig.smoothWorld = value;
        else if ("fastRender".equals(key)) PotassiumConfig.fastRender = value;
        else if ("fastMath".equals(key)) PotassiumConfig.fastMath = value;
        else if ("lazyChunkLoading".equals(key)) PotassiumConfig.lazyChunkLoading = value;
        else if ("renderRegions".equals(key)) PotassiumConfig.renderRegions = value;
        else if ("smartAnimations".equals(key)) PotassiumConfig.smartAnimations = value;
        else if ("skipEmptyDrawCalls".equals(key)) PotassiumConfig.skipEmptyDrawCalls = value;
        else if ("dynamicChunkUpdates".equals(key)) PotassiumConfig.dynamicChunkUpdates = value;
    }

    private String label(String name, boolean enabled) {
        return name + "  [" + (enabled ? "ON" : "OFF") + "]";
    }

    private void updateTopButtons() {
        masterButton.displayString = "ALL OPTIMIZATION  ["
                + (PerformanceManager.isOptimizationEnabled() ? "ON" : "OFF") + "]";
        profileButton.displayString = "PROFILE: "
                + PerformanceProfileManager.getActiveProfileName();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == CLOSE_ID) {
            mc.displayGuiScreen(null);
            return;
        }

        if (button.id == RESET_ID) {
            PotassiumDevDiagnostics.resetBenchmark();
            return;
        }

        if (button.id == MASTER_ID) {
            boolean next = !PerformanceManager.isOptimizationEnabled();
            PerformanceManager.setOptimizationsEnabled(next);
            PotassiumDevDiagnostics.startBenchmark(next);
            updateTopButtons();
            return;
        }

        if (button.id == PROFILE_ID) {
            PerformanceProfile current = PerformanceProfileManager.getActiveProfile();
            PerformanceProfile[] profiles = PerformanceProfile.values();
            int nextIndex = (current.ordinal() + 1) % profiles.length;
            PotassiumConfig.performanceProfile = profiles[nextIndex].name();
            PerformanceProfileManager.applyConfiguredProfile();
            saveConfig();
            updateTopButtons();
            refreshToggleButtons();
            return;
        }

        if (button.id >= 10 && button.id < 10 + TOGGLES.length) {
            int index = button.id - 10;
            String key = TOGGLES[index][1];
            setToggle(key, !getToggle(key));
            button.displayString = label(TOGGLES[index][0], getToggle(key));
            saveConfig();
        }
    }

    private void refreshToggleButtons() {
        for (int i = 0; i < TOGGLES.length; i++) {
            for (Object obj : buttonList) {
                GuiButton button = (GuiButton) obj;
                if (button.id == 10 + i) {
                    button.displayString = label(TOGGLES[i][0], getToggle(TOGGLES[i][1]));
                    break;
                }
            }
        }
    }

    private void saveConfig() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj, "POTASSIUM DEV PANEL",
                width / 2, 10, 0xFFFFFF);
        drawCenteredString(fontRendererObj,
                "Dev Temp V1  |  FPS telemetry stays ON",
                width / 2, 22, 0xAAAAAA);

        drawString(fontRendererObj, "RENDERING", width / 2 - 225, 79, 0xFFFFFF);
        drawString(fontRendererObj, "CPU / MEMORY", width / 2 - 225, 123, 0xFFFFFF);
        drawString(fontRendererObj, "WORLD / ENGINE", width / 2 - 225, 167, 0xFFFFFF);

        int benchmarkY = 225;
        drawString(fontRendererObj, "BENCHMARK", width / 2 - 225, benchmarkY, 0xFFFFFF);

        drawString(fontRendererObj, "FPS Counter", width / 2 - 225, benchmarkY + 14, 0xAAAAAA);
        drawString(fontRendererObj, "[ ON ]", width / 2 - 125, benchmarkY + 14, 0xFFFFFF);

        drawString(fontRendererObj, "Average FPS", width / 2 - 225, benchmarkY + 28, 0xAAAAAA);
        drawString(fontRendererObj, "[ ON ]", width / 2 - 125, benchmarkY + 28, 0xFFFFFF);

        drawString(fontRendererObj, "1% Low", width / 2 - 225, benchmarkY + 42, 0xAAAAAA);
        drawString(fontRendererObj, "[ ON ]", width / 2 - 125, benchmarkY + 42, 0xFFFFFF);

        drawString(fontRendererObj, "Frame Time", width / 2 - 225, benchmarkY + 56, 0xAAAAAA);
        drawString(fontRendererObj, "[ ON ]", width / 2 - 125, benchmarkY + 56, 0xFFFFFF);

        int right = width / 2 + 5;
        drawString(fontRendererObj, "WITHOUT OPT:", right, benchmarkY + 14, 0xFFFFFF);
        drawString(fontRendererObj, formatFps(PotassiumDevDiagnostics.getBaselineAverageFps()),
                right + 100, benchmarkY + 14, 0xFFFFFF);

        drawString(fontRendererObj, "WITH OPT:", right, benchmarkY + 28, 0xFFFFFF);
        drawString(fontRendererObj, formatFps(PotassiumDevDiagnostics.getOptimizedAverageFps()),
                right + 100, benchmarkY + 28, 0xFFFFFF);

        drawString(fontRendererObj, "DIFFERENCE:", right, benchmarkY + 42, 0xFFFFFF);
        drawString(fontRendererObj, formatDifference(), right + 100, benchmarkY + 42, 0xFFFFFF);

        drawString(fontRendererObj, "STATUS:", right, benchmarkY + 56, 0xFFFFFF);
        drawString(fontRendererObj, PotassiumDevDiagnostics.getBenchmarkStatus(),
                right + 100, benchmarkY + 56, 0xAAAAAA);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String formatFps(int fps) {
        return fps > 0 ? fps + " FPS" : "-- FPS";
    }

    private String formatDifference() {
        int difference = PotassiumDevDiagnostics.getFpsDifference();
        double percent = PotassiumDevDiagnostics.getFpsGainPercent();
        if (difference == 0) return "-- FPS";
        return (difference > 0 ? "+" : "") + difference + " FPS ("
                + String.format("%.1f", percent) + "%)";
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
