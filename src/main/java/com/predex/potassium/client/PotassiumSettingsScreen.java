package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public final class PotassiumSettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private GuiButton masterButton;
    private GuiButton profileButton;
    private GuiButton videoButton;

    public PotassiumSettingsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        int center = width / 2;
        masterButton = new GuiButton(1, center - 155, height / 6 + 10, 310, 20,
                masterText());
        profileButton = new GuiButton(2, center - 155, height / 6 + 35, 310, 20,
                "Profile: " + PotassiumConfig.performanceProfile);
        videoButton = new GuiButton(3, center - 155, height / 6 + 60, 310, 20,
                "Potassium Video Settings");
        buttonList.add(masterButton);
        buttonList.add(profileButton);
        buttonList.add(videoButton);

        buttonList.add(new GuiButton(10, center - 155, height / 6 + 90, 150, 20,
                "Rendering"));
        buttonList.add(new GuiButton(11, center + 5, height / 6 + 90, 150, 20,
                "Chunk Engine"));
        buttonList.add(new GuiButton(12, center - 155, height / 6 + 115, 150, 20,
                "Entities"));
        buttonList.add(new GuiButton(13, center + 5, height / 6 + 115, 150, 20,
                "Particles"));
        buttonList.add(new GuiButton(14, center - 155, height / 6 + 140, 150, 20,
                "Adaptive Performance"));
        buttonList.add(new GuiButton(15, center + 5, height / 6 + 140, 150, 20,
                "Memory / CPU"));

        buttonList.add(new GuiButton(99, center - 100, height - 28, 200, 20,
                I18n.format("gui.done")));
    }

    private String masterText() {
        return "Optimization: " + (PotassiumConfig.enabled ? "ON" : "OFF")
                + "  |  FPS monitor: ON";
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            PotassiumConfig.enabled = !PotassiumConfig.enabled;
            save();
            masterButton.displayString = masterText();
        } else if (button.id == 2) {
            cycleProfile();
            save();
            profileButton.displayString = "Profile: " + PotassiumConfig.performanceProfile;
        } else if (button.id == 3) {
            Minecraft.getMinecraft().displayGuiScreen(
                    new PotassiumVideoSettingsScreen(this));
        } else if (button.id == 10) {
            toggleRendering();
            save();
        } else if (button.id == 11) {
            toggleChunks();
            save();
        } else if (button.id == 12) {
            PotassiumConfig.optimizeEntityRendering = !PotassiumConfig.optimizeEntityRendering;
            save();
        } else if (button.id == 13) {
            PotassiumConfig.reduceParticles = !PotassiumConfig.reduceParticles;
            save();
        } else if (button.id == 14) {
            PotassiumConfig.adaptivePerformance = !PotassiumConfig.adaptivePerformance;
            save();
        } else if (button.id == 15) {
            PotassiumConfig.lowMemoryMode = !PotassiumConfig.lowMemoryMode;
            save();
        } else if (button.id == 99) {
            Minecraft.getMinecraft().displayGuiScreen(parent);
        }
    }

    private void toggleRendering() {
        PotassiumConfig.fastRender = !PotassiumConfig.fastRender;
        PotassiumConfig.blockFaceCulling = PotassiumConfig.fastRender;
        PotassiumConfig.entityOcclusionCulling = PotassiumConfig.fastRender;
        PotassiumConfig.smartAnimations = PotassiumConfig.fastRender;
    }

    private void toggleChunks() {
        PotassiumConfig.optimizeChunkUpdates = !PotassiumConfig.optimizeChunkUpdates;
        PotassiumConfig.lazyChunkLoading = PotassiumConfig.optimizeChunkUpdates;
    }

    private void cycleProfile() {
        if ("ULTRA_LOW".equals(PotassiumConfig.performanceProfile)) {
            PotassiumConfig.performanceProfile = "LOW_END";
        } else if ("LOW_END".equals(PotassiumConfig.performanceProfile)) {
            PotassiumConfig.performanceProfile = "BALANCED";
        } else {
            PotassiumConfig.performanceProfile = "ULTRA_LOW";
        }
    }

    private void save() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Potassium Settings", width / 2, 25, 0xFFFFFF);
        drawCenteredString(fontRendererObj,
                "Performance controls are separate from FPS monitoring.",
                width / 2, 45, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
