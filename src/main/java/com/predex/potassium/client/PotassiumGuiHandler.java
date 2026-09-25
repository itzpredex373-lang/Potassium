package com.predex.potassium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class PotassiumGuiHandler {
    private static final int POTASSIUM_SETTINGS_ID = 19001;
    private static final int VIDEO_SETTINGS_ID = 101;

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (!(event.getGui() instanceof GuiOptions)) return;

        int x = event.getGui().width / 2 - 155;
        int y = event.getGui().height / 6 + 72;

        event.getButtonList().add(
                new net.minecraft.client.gui.GuiButton(
                        POTASSIUM_SETTINGS_ID,
                        x,
                        y,
                        310,
                        20,
                        "Potassium Settings"));
    }

    @SubscribeEvent
    public void onAction(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        if (!(event.getGui() instanceof GuiOptions)) return;

        if (event.getButton().id == POTASSIUM_SETTINGS_ID) {
            open(new PotassiumSettingsScreen(event.getGui()));
            event.setCanceled(true);
        } else if (event.getButton().id == VIDEO_SETTINGS_ID) {
            open(new PotassiumVideoSettingsScreen(event.getGui()));
            event.setCanceled(true);
        }
    }

    private void open(GuiScreen screen) {
        Minecraft.getMinecraft().displayGuiScreen(screen);
    }
}
