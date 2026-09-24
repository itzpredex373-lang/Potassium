package com.predex.potassium.dev;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.benchmark.PotassiumDevDiagnostics;
import com.predex.potassium.optimization.profile.PerformanceProfile;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public final class PotassiumDevScreen extends GuiScreen {
    private static final int MASTER_ID = 1;
    private static final int PROFILE_ID = 2;
    private static final int CLOSE_ID = 99;

    private GuiButton masterButton;
    private GuiButton profileButton;

    @Override
    public void initGui() {
        buttonList.clear();
        int left = width / 2 - 220;
        int y = 42;
        int columnWidth = 145;
        int gap = 8;

        masterButton = new GuiButton(MASTER_ID, width / 2 - 220, y, 145, 20, "");
        profileButton = new GuiButton(PROFILE_ID, width / 2 + 75, y, 145, 20, "");
        buttonList.add(masterButton);
        buttonList.add(profileButton);

        String[][] toggles = new String[][] {
                {"Particles", "reduceParticles"},
                {"Entity Render", "optimizeEntityRendering"},
                {"Entity Updates", "reduceEntityUpdates"},
                {"Chunk Updates", "optimizeChunkUpdates"},
                {"Renderer Hooks", "rendererCoreHooks"},
                {"Block Culling", "blockFaceCulling"},
                {"Entity Occlusion", "entityOcclusionCulling"},
                {"Adaptive Performance", "adaptivePerformance"},
                {"Low Memory Mode", "lowMemoryMode"},
                {"Smooth FPS", "smoothFps"},
                {"Smooth World", "smoothWorld"},
                {"Fast Render", "fastRender"},
                {"Fast Math", "fastMath"},
                {"Lazy Chunks", "lazyChunkLoading"},
                {"Render Regions", "renderRegions"},
                {"Smart Animations", "smartAnimations"},
                {"Empty Draw Skip", "skipEmptyDrawCalls"},
                {"Dynamic Chunks", "dynamicChunkUpdates"}
        };

        for (int i = 0; i < toggles.length; i++) {
            int column = i % 3;
            int row = i / 3;
            int bx = left + column * (columnWidth + gap);
            int by = y + 28 + row * 23;
            buttonList.add(new GuiButton(10 + i, bx, by, 145, 20,
                    label(toggles[i][0], getToggle(toggles[i][1]))));
        }

        buttonList.add(new GuiButton(CLOSE_ID, width / 2 - 75, y + 28 + 6 * 23, 150, 20, "Close"));
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
        return name + ": " + (enabled ? "ON" : "OFF");
    }

    private void updateTopButtons() {
        masterButton.displayString = "ALL OPT: "
                + (PerformanceManager.isOptimizationEnabled() ? "ON" : "OFF");
        profileButton.displayString = "Profile: "
                + PerformanceProfileManager.getActiveProfileName();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == CLOSE_ID) {
            mc.displayGuiScreen(null);
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
            return;
        }

        if (button.id >= 10 && button.id < 28) {
            int index = button.id - 10;
            String[][] keys = new String[][] {
                    {"Particles", "reduceParticles"},
                    {"Entity Render", "optimizeEntityRendering"},
                    {"Entity Updates", "reduceEntityUpdates"},
                    {"Chunk Updates", "optimizeChunkUpdates"},
                    {"Renderer Hooks", "rendererCoreHooks"},
                    {"Block Culling", "blockFaceCulling"},
                    {"Entity Occlusion", "entityOcclusionCulling"},
                    {"Adaptive Performance", "adaptivePerformance"},
                    {"Low Memory Mode", "lowMemoryMode"},
                    {"Smooth FPS", "smoothFps"},
                    {"Smooth World", "smoothWorld"},
                    {"Fast Render", "fastRender"},
                    {"Fast Math", "fastMath"},
                    {"Lazy Chunks", "lazyChunkLoading"},
                    {"Render Regions", "renderRegions"},
                    {"Smart Animations", "smartAnimations"},
                    {"Empty Draw Skip", "skipEmptyDrawCalls"},
                    {"Dynamic Chunks", "dynamicChunkUpdates"}
            };
            String key = keys[index][1];
            setToggle(key, !getToggle(key));
            button.displayString = label(keys[index][0], getToggle(key));
            saveConfig();
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
        drawCenteredString(fontRendererObj, "POTASSIUM DEV PANEL", width / 2, 12, 0xFFFFFF);
        drawCenteredString(fontRendererObj,
                "ALL OPT toggles optimization only; FPS telemetry stays ON",
                width / 2, 28, 0xAAAAAA);

        drawCenteredString(fontRendererObj,
                "Benchmark: " + PotassiumDevDiagnostics.getBenchmarkStatus()
                        + " | OFF Avg: " + PotassiumDevDiagnostics.getBaselineAverageFps()
                        + " | ON Avg: " + PotassiumDevDiagnostics.getOptimizedAverageFps()
                        + " | Diff: " + PotassiumDevDiagnostics.getFpsDifference()
                        + " (" + String.format("%.1f", PotassiumDevDiagnostics.getFpsGainPercent()) + "%)",
                width / 2, height - 26, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
