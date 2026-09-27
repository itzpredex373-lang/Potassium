package com.predex.potassium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Navigation hooks for Potassium.
 *
 * Video Settings intentionally uses Minecraft's native 1.8.9 GuiVideoSettings
 * so the controls, scrolling and layout remain identical to vanilla.
 */
public final class PotassiumGuiHandler {
    private static final int POTASSIUM_SETTINGS_ID = 19001;
    private static final int POTASSIUM_VIDEO_BUTTON_ID = 19002;
    private static final int VIDEO_SETTINGS_ID = 101;

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        GuiScreen gui = event.gui;

        if (gui instanceof GuiOptions) {
            int x = gui.width / 2 - 155;
            int y = gui.height - 52;

            event.buttonList.add(new GuiButton(
                    POTASSIUM_SETTINGS_ID, x, y, 310, 20, "Potassium Settings"));
            return;
        }

        if (gui instanceof GuiVideoSettings) {
            event.buttonList.add(new GuiButton(
                    POTASSIUM_VIDEO_BUTTON_ID,
                    gui.width / 2 - 155,
                    gui.height - 52,
                    310,
                    20,
                    "Potassium Performance"));
        }
    }

    @SubscribeEvent
    public void onAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (event.gui instanceof GuiOptions
                && event.button.id == POTASSIUM_SETTINGS_ID) {
            Minecraft.getMinecraft().displayGuiScreen(
                    new PotassiumSettingsScreen(event.gui));
            event.setCanceled(true);
            return;
        }

        if (event.gui instanceof GuiOptions
                && event.button.id == VIDEO_SETTINGS_ID) {
            // Use the real Minecraft 1.8.9 video screen instead of a custom
            // approximation. This fixes the old broken-looking video page.
            Minecraft.getMinecraft().displayGuiScreen(
                    new GuiVideoSettings(event.gui,
                            Minecraft.getMinecraft().gameSettings));
            event.setCanceled(true);
            return;
        }

        if (event.gui instanceof GuiVideoSettings
                && event.button.id == POTASSIUM_VIDEO_BUTTON_ID) {
            Minecraft.getMinecraft().displayGuiScreen(
                    new PotassiumSettingsScreen(event.gui));
            event.setCanceled(true);
        }
    }
}
