package com.predex.potassium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Adds Potassium controls to the vanilla Options screen without replacing
 * vanilla controls. Button 101 is the documented Minecraft 1.8.9 Video Settings
 * button, so only that navigation action is intercepted.
 */
public final class PotassiumGuiHandler {
    private static final int POTASSIUM_SETTINGS_ID = 19001;
    private static final int VIDEO_SETTINGS_ID = 101;

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiOptions)) {
            return;
        }

        GuiScreen gui = event.getGui();
        int x = gui.width / 2 - 155;
        int y = gui.height - 52;

        event.getButtonList().add(
                new GuiButton(
                        POTASSIUM_SETTINGS_ID,
                        x,
                        y,
                        310,
                        20,
                        "Potassium Settings"));
    }

    @SubscribeEvent
    public void onAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (!(event.getGui() instanceof GuiOptions)) {
            return;
        }

        int id = event.getButton().id;

        if (id == POTASSIUM_SETTINGS_ID) {
            Minecraft.getMinecraft().displayGuiScreen(
                    new PotassiumSettingsScreen(event.getGui()));
            event.setCanceled(true);
        } else if (id == VIDEO_SETTINGS_ID) {
            Minecraft.getMinecraft().displayGuiScreen(
                    new PotassiumVideoSettingsScreen(event.getGui()));
            event.setCanceled(true);
        }
    }
}
