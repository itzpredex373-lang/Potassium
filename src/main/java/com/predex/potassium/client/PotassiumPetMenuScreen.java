package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
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
                PotassiumConfig.miniPetEnabled = !PotassiumConfig.miniPetEnabled;
                break;
            case 2:
                PotassiumConfig.miniPetScale = cycle(PotassiumConfig.miniPetScale, 25, 75, 8);
                break;
            case 3:
                cyclePetType();
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
        String[] types = {"predex","wolf","dragon","devil","blaze","slime",
                "endermite","bat","chicken","rabbit","ocelot"};
        int current = 0;
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(PotassiumConfig.miniPetType)) { current = i; break; }
        }
        PotassiumConfig.miniPetType = types[(current + 1) % types.length];
    }

    private int cycle(int value, int min, int max, int step) {
        int next = value + step;
        return next > max ? min : next;
    }

    private String petName(String type) {
        if ("predex".equals(type)) return "Predex Pet";
        if ("dragon".equals(type)) return "Mini King Dragon";
        if ("devil".equals(type)) return "Mini Devil";
        if ("wolf".equals(type)) return "Wolf";
        if ("blaze".equals(type)) return "Blaze";
        if ("slime".equals(type)) return "Slime";
        if ("endermite".equals(type)) return "Endermite";
        if ("bat".equals(type)) return "Bat";
        if ("chicken".equals(type)) return "Chicken";
        if ("rabbit".equals(type)) return "Rabbit";
        if ("ocelot".equals(type)) return "Ocelot";
        return type;
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
                "Server-visible mode requires Potassium on the server too.",
                width / 2, 154, 0x777777);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}