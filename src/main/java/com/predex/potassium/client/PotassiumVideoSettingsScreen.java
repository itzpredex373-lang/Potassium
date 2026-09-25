package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionsRowList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;

import java.io.IOException;

/**
 * Potassium's OptiFine-style video page.
 *
 * The first page is the complete vanilla 1.8.9 video option set, presented
 * using the same two-column/scrolling control model as the vanilla video GUI.
 * Potassium-specific performance controls live in Potassium Settings so the
 * two configuration layers never fight over the same option.
 */
public final class PotassiumVideoSettingsScreen extends GuiScreen {
    private static final GameSettings.Options[] VIDEO_OPTIONS = new GameSettings.Options[] {
            GameSettings.Options.GRAPHICS,
            GameSettings.Options.RENDER_DISTANCE,
            GameSettings.Options.AMBIENT_OCCLUSION,
            GameSettings.Options.FRAMERATE_LIMIT,
            GameSettings.Options.ANAGLYPH,
            GameSettings.Options.VIEW_BOBBING,
            GameSettings.Options.GUI_SCALE,
            GameSettings.Options.GAMMA,
            GameSettings.Options.RENDER_CLOUDS,
            GameSettings.Options.PARTICLES,
            GameSettings.Options.USE_FULLSCREEN,
            GameSettings.Options.ENABLE_VSYNC,
            GameSettings.Options.MIPMAP_LEVELS,
            GameSettings.Options.BLOCK_ALTERNATIVES,
            GameSettings.Options.USE_VBO,
            GameSettings.Options.ENTITY_SHADOWS
    };

    private final GuiScreen parent;
    private final GameSettings gameSettings;
    private GuiOptionsRowList optionsRowList;

    public PotassiumVideoSettingsScreen(GuiScreen parent) {
        this.parent = parent;
        this.gameSettings = Minecraft.getMinecraft().gameSettings;
    }

    @Override
    public void initGui() {
        buttonList.clear();

        optionsRowList = new GuiOptionsRowList(
                Minecraft.getMinecraft(),
                width,
                height,
                32,
                height - 58,
                25,
                VIDEO_OPTIONS);

        buttonList.add(new GuiButton(
                300,
                width / 2 - 155,
                height - 52,
                150,
                20,
                "Potassium Performance"));

        buttonList.add(new GuiButton(
                301,
                width / 2 + 5,
                height - 52,
                150,
                20,
                "Fast Render: " + onOff(PotassiumConfig.fastRender)));

        buttonList.add(new GuiButton(
                200,
                width / 2 - 100,
                height - 27,
                200,
                20,
                "Done"));
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (optionsRowList != null) {
            optionsRowList.handleMouseInput();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int oldGuiScale = gameSettings.guiScale;
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (optionsRowList != null) {
            optionsRowList.mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (gameSettings.guiScale != oldGuiScale) {
            net.minecraft.client.gui.ScaledResolution resolution =
                    new net.minecraft.client.gui.ScaledResolution(Minecraft.getMinecraft());
            setWorldAndResolution(Minecraft.getMinecraft(),
                    resolution.getScaledWidth(),
                    resolution.getScaledHeight());
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        int oldGuiScale = gameSettings.guiScale;
        super.mouseReleased(mouseX, mouseY, state);

        if (optionsRowList != null) {
            optionsRowList.mouseReleased(mouseX, mouseY, state);
        }

        if (gameSettings.guiScale != oldGuiScale) {
            net.minecraft.client.gui.ScaledResolution resolution =
                    new net.minecraft.client.gui.ScaledResolution(Minecraft.getMinecraft());
            setWorldAndResolution(Minecraft.getMinecraft(),
                    resolution.getScaledWidth(),
                    resolution.getScaledHeight());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 200) {
            gameSettings.saveOptions();
            Minecraft.getMinecraft().displayGuiScreen(parent);
        } else if (button.id == 300) {
            gameSettings.saveOptions();
            Minecraft.getMinecraft().displayGuiScreen(
                    new PotassiumSettingsScreen(this));
        } else if (button.id == 301) {
            PotassiumConfig.fastRender = !PotassiumConfig.fastRender;
            savePotassium();
            button.displayString = "Fast Render: " + onOff(PotassiumConfig.fastRender);
        }
    }

    private void savePotassium() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    private String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        if (optionsRowList != null) {
            optionsRowList.drawScreen(mouseX, mouseY, partialTicks);
        }

        drawCenteredString(fontRendererObj,
                "Potassium Video Settings",
                width / 2,
                12,
                0xFFFFFF);

        drawCenteredString(fontRendererObj,
                "Minecraft 1.8.9 video controls + Potassium performance",
                width / 2,
                24,
                0xAAAAAA);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
