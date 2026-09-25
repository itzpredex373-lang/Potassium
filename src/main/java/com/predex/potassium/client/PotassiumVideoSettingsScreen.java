package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;

import java.io.IOException;

public final class PotassiumVideoSettingsScreen extends GuiScreen {
    private final GuiScreen parent;
    private final GameSettings gameSettings;

    public PotassiumVideoSettingsScreen(GuiScreen parent) {
        this.parent = parent;
        this.gameSettings = Minecraft.getMinecraft().gameSettings;
    }

    @Override
    public void initGui() {
        buttonList.clear();
        int center = width / 2;
        int y = 45;

        buttonList.add(new GuiButton(1, center - 155, y, 310, 20, graphicsText()));
        buttonList.add(new GuiButton(2, center - 155, y + 24, 150, 20, renderDistanceText()));
        buttonList.add(new GuiButton(3, center + 5, y + 24, 150, 20, "Smooth FPS: " + onOff(PotassiumConfig.smoothFps)));
        buttonList.add(new GuiButton(4, center - 155, y + 48, 150, 20, "Smooth World: " + onOff(PotassiumConfig.smoothWorld)));
        buttonList.add(new GuiButton(5, center + 5, y + 48, 150, 20, "Fast Render: " + onOff(PotassiumConfig.fastRender)));
        buttonList.add(new GuiButton(6, center - 155, y + 72, 150, 20, "Fast Math: " + onOff(PotassiumConfig.fastMath)));
        buttonList.add(new GuiButton(7, center + 5, y + 72, 150, 20, "Smart Animations: " + onOff(PotassiumConfig.smartAnimations)));
        buttonList.add(new GuiButton(8, center - 155, y + 96, 150, 20, "Chunk Loading: " + chunkText()));
        buttonList.add(new GuiButton(9, center + 5, y + 96, 150, 20, "Render Regions: " + onOff(PotassiumConfig.renderRegions)));
        buttonList.add(new GuiButton(10, center - 155, y + 120, 150, 20, "Block Face Culling: " + onOff(PotassiumConfig.blockFaceCulling)));
        buttonList.add(new GuiButton(11, center + 5, y + 120, 150, 20, "Entity Culling: " + onOff(PotassiumConfig.entityOcclusionCulling)));
        buttonList.add(new GuiButton(99, center - 100, height - 28, 200, 20, "Done"));
    }

    private String graphicsText() {
        return "Graphics: " + (gameSettings.fancyGraphics ? "Fancy" : "Fast");
    }

    private String renderDistanceText() {
        return "Render Distance: " + gameSettings.renderDistanceChunks;
    }

    private String chunkText() {
        return PotassiumConfig.lazyChunkLoading ? "Lazy" : "Immediate";
    }

    private String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                gameSettings.fancyGraphics = !gameSettings.fancyGraphics;
                gameSettings.saveOptions();
                button.displayString = graphicsText();
                break;
            case 2:
                int distance = gameSettings.renderDistanceChunks + 2;
                if (distance > 16) distance = 2;
                gameSettings.renderDistanceChunks = distance;
                gameSettings.saveOptions();
                button.displayString = renderDistanceText();
                break;
            case 3:
                PotassiumConfig.smoothFps = !PotassiumConfig.smoothFps;
                break;
            case 4:
                PotassiumConfig.smoothWorld = !PotassiumConfig.smoothWorld;
                break;
            case 5:
                PotassiumConfig.fastRender = !PotassiumConfig.fastRender;
                break;
            case 6:
                PotassiumConfig.fastMath = !PotassiumConfig.fastMath;
                break;
            case 7:
                PotassiumConfig.smartAnimations = !PotassiumConfig.smartAnimations;
                break;
            case 8:
                PotassiumConfig.lazyChunkLoading = !PotassiumConfig.lazyChunkLoading;
                break;
            case 9:
                PotassiumConfig.renderRegions = !PotassiumConfig.renderRegions;
                break;
            case 10:
                PotassiumConfig.blockFaceCulling = !PotassiumConfig.blockFaceCulling;
                break;
            case 11:
                PotassiumConfig.entityOcclusionCulling = !PotassiumConfig.entityOcclusionCulling;
                break;
            case 99:
                Minecraft.getMinecraft().displayGuiScreen(parent);
                return;
            default:
                break;
        }
        save();
        initGui();
    }

    private void save() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Potassium Video Settings", width / 2, 20, 0xFFFFFF);
        drawCenteredString(fontRendererObj,
                "OptiFine-style controls backed by Potassium's actual options.",
                width / 2, 32, 0xAAAAAA);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
