package com.predex.potassium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

/**
 * Opens Minecraft 1.8.9's native Video Settings screen from Potassium.
 * Using the vanilla screen keeps graphics options compatible with Forge.
 */
public final class PotassiumVideoSettingsScreen extends GuiVideoSettings {
    public PotassiumVideoSettingsScreen(GuiScreen parent) {
        super(parent, Minecraft.getMinecraft().gameSettings);
    }
}
