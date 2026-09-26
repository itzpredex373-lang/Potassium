package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.client.PotassiumPetTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import java.io.IOException;

public final class PotassiumPetMenuScreen extends GuiScreen {
    private final GuiScreen parent;

    public PotassiumPetMenuScreen(GuiScreen parent) { this.parent = parent; }

    public static void open() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(new PotassiumPetMenuScreen(mc.currentScreen));
    }

    @Override
    public void initGui() {
        if (!PerformanceProfileManager.isPetAllowed()) {
            Minecraft.getMinecraft().displayGuiScreen(parent);
            return;
        }
        buttonList.clear();
        int center = width / 2;
        int left = center - 155;
        int right = center + 5;

        buttonList.add(new GuiButton(1, left, 55, 150, 20,
                "Mini Pet: " + onOff(PotassiumConfig.miniPetEnabled)));
        buttonList.add(new GuiButton(2, right, 55, 150, 20,
                "Pet Scale: " + PotassiumConfig.miniPetScale + "%"));
        buttonList.add(new GuiButton(3, left, 80, 310, 20,
                "Pet: " + petName(PotassiumConfig.miniPetType)));
        buttonList.add(new GuiButton(4, center - 155, 110, 310, 20,
                "Open Potassium Settings"));
        buttonList.add(new GuiButton(5, center - 50, height - 28, 100, 20, "Done"));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                if (PerformanceProfileManager.isPetAllowed()) {
                    PotassiumConfig.miniPetEnabled = !PotassiumConfig.miniPetEnabled;
                }
                break;
            case 2:
                if (PerformanceProfileManager.isPetAllowed()) {
                    PotassiumConfig.miniPetScale = cycle(PotassiumConfig.miniPetScale, 25, 75, 8);
                }
                break;
            case 3:
                if (PerformanceProfileManager.isPetAllowed()) {
                    cyclePetType();
                }
                syncPet();
                break;
            case 4:
                Minecraft.getMinecraft().displayGuiScreen(new PotassiumSettingsScreen(this));
                return;
            case 5:
                Minecraft.getMinecraft().displayGuiScreen(parent);
                return;
            default:
                return;
        }
        save();
        initGui();
    }

    private void cyclePetType() {
        PotassiumConfig.miniPetType = PotassiumPetTypes.TYPES[
                (PotassiumPetTypes.indexOf(PotassiumConfig.miniPetType) + 1)
                        % PotassiumPetTypes.TYPES.length];
    }

    private int cycle(int value, int min, int max, int step) {
        int next = value + step;
        return next > max ? min : next;
    }

    private String petName(String type) {
        return PotassiumPetTypes.displayName(type);
    }

    private String onOff(boolean value) { return value ? "ON" : "OFF"; }

    private void save() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Potassium • Pet Menu", width / 2, 20, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "Predex Pet System", width / 2, 38, 0xAAAAAA);
        drawCenteredString(fontRendererObj,
                "Open with /pet or your Potassium Pet Menu keybind.",
                width / 2, 140, 0xAAAAAA);
        drawCenteredString(fontRendererObj,
                "Client-only • visible only to you.",
                width / 2, 154, 0x777777);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}