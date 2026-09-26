package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;
import java.util.List;

public final class PotassiumPetMenuScreen extends GuiScreen {
    private final GuiScreen parent;
    private GuiTextField importPath;
    private String status = "";

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

        PotassiumPetPackManager.init();
        buttonList.clear();
        int center = width / 2;
        int left = center - 155;
        int right = center + 5;

        buttonList.add(new GuiButton(1, left, 55, 150, 20,
                "Mini Pet: " + onOff(PotassiumConfig.miniPetEnabled)));
        buttonList.add(new GuiButton(2, right, 55, 150, 20,
                "Pet Scale: " + PotassiumConfig.miniPetScale + "%"));
        buttonList.add(new GuiButton(3, left, 80, 310, 20,
                "Pet: " + PotassiumPetPackManager.getDisplayName(PotassiumConfig.miniPetType)));

        importPath = new GuiTextField(10, fontRendererObj, left, 108, 230, 20);
        importPath.setMaxStringLength(260);
        importPath.setText("");
        buttonList.add(new GuiButton(6, right, 108, 80, 20, "Import"));
        buttonList.add(new GuiButton(7, left, 133, 310, 20, "Refresh Pet Packs"));

        buttonList.add(new GuiButton(4, left, 158, 310, 20,
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
                    PotassiumConfig.miniPetScale =
                            cycle(PotassiumConfig.miniPetScale, 25, 75, 8);
                }
                break;
            case 3:
                if (PerformanceProfileManager.isPetAllowed()) cyclePetType();
                break;
            case 6:
                if (importPath != null && PotassiumPetPackManager.importPack(importPath.getText())) {
                    status = "Pet imported successfully.";
                    cyclePetTypeToFirstImported();
                } else {
                    status = "Import failed. Use a valid .potpet file.";
                }
                break;
            case 7:
                PotassiumPetPackManager.reload();
                status = "Pet packs refreshed.";
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
        List<String> ids = PotassiumPetPackManager.getPetIds();
        if (ids.isEmpty()) return;
        int index = ids.indexOf(PotassiumConfig.miniPetType);
        PotassiumConfig.miniPetType = ids.get((index + 1 + ids.size()) % ids.size());
    }

    private void cyclePetTypeToFirstImported() {
        List<String> ids = PotassiumPetPackManager.getPetIds();
        if (ids.size() > PotassiumPetTypes.TYPES.length) {
            PotassiumConfig.miniPetType = ids.get(PotassiumPetTypes.TYPES.length);
        }
    }

    private int cycle(int value, int min, int max, int step) {
        int next = value + step;
        return next > max ? min : next;
    }

    private String onOff(boolean value) { return value ? "ON" : "OFF"; }

    private void save() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (importPath != null && importPath.textboxKeyTyped(typedChar, keyCode)) return;
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (importPath != null) importPath.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (importPath != null) importPath.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Potassium • Pet Menu", width / 2, 20, 0xFFFFFF);
        drawCenteredString(fontRendererObj, "20 Built-in + Imported/Child Pets", width / 2, 38, 0xAAAAAA);

        if (importPath != null) {
            importPath.drawTextBox();
            drawString(fontRendererObj, "Path to .potpet:", width / 2 - 155, 98, 0x777777);
        }

        drawCenteredString(fontRendererObj, status, width / 2, 184, 0xAAAAAA);
        drawCenteredString(fontRendererObj,
                "Client-only • pets never join the server.",
                width / 2, 200, 0x777777);
        drawCenteredString(fontRendererObj,
                "Pack folder: config/potassium/pets",
                width / 2, 216, 0x777777);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
