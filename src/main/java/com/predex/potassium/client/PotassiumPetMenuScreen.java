package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.List;

/**
 * Client-only pet selector inspired by the familiar SkyBlock-style inventory
 * layout, while using Potassium's own UI and original pet assets.
 */
public final class PotassiumPetMenuScreen extends GuiScreen {
    private final GuiScreen parent;
    private String status = "";

    public PotassiumPetMenuScreen(GuiScreen parent) {
        this.parent = parent;
    }

    public static void open() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.displayGuiScreen(new PotassiumPetMenuScreen(mc.currentScreen));
    }

    @Override
    public void initGui() {
        PotassiumPetPackManager.init();
        buttonList.clear();

        int center = width / 2;
        int top = Math.max(42, height / 2 - 95);
        List<String> ids = PotassiumPetPackManager.getPetIds();

        // 5 x 3 inventory-like pet grid.
        int slotW = 82;
        int slotH = 28;
        int startX = center - (slotW * 5) / 2;
        int gridTop = top + 30;

        int max = Math.min(15, ids.size());
        for (int i = 0; i < max; i++) {
            int row = i / 5;
            int col = i % 5;
            String id = ids.get(i);
            String name = PotassiumPetPackManager.getDisplayName(id);
            if (name.length() > 13) name = name.substring(0, 13);

            String marker = id.equals(PotassiumConfig.miniPetType) ? "✓ " : "";
            buttonList.add(new GuiButton(
                    100 + i,
                    startX + col * slotW,
                    gridTop + row * slotH,
                    slotW - 2,
                    slotH - 2,
                    marker + name));
        }

        int controlsY = gridTop + 3 * slotH + 8;
        addButton(1, center - 155, controlsY, 150,
                "Mini Pet: " + onOff(PotassiumConfig.miniPetEnabled));
        addButton(2, center + 5, controlsY, 150,
                "Pet Scale: " + PotassiumConfig.miniPetScale + "%");

        addButton(4, center - 155, controlsY + 24, 150,
                "Potassium Settings");
        addButton(5, center + 5, controlsY + 24, 150,
                "Done");

        addButton(7, center - 155, controlsY + 48, 310,
                "Refresh Pet Packs");

        if (!PerformanceProfileManager.isPetAllowed()) {
            GuiButton petToggle = getButton(1);
            GuiButton scale = getButton(2);
            if (petToggle != null) petToggle.enabled = false;
            if (scale != null) scale.enabled = false;
        }
    }

    private void addButton(int id, int x, int y, int width, String text) {
        buttonList.add(new GuiButton(id, x, y, width, 20, text));
    }

    private GuiButton getButton(int id) {
        for (Object object : buttonList) {
            if (object instanceof GuiButton && ((GuiButton) object).id == id) {
                return (GuiButton) object;
            }
        }
        return null;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id >= 100 && button.id < 115) {
            List<String> ids = PotassiumPetPackManager.getPetIds();
            int index = button.id - 100;
            if (index >= 0 && index < ids.size()) {
                PotassiumConfig.miniPetType = ids.get(index);
                // Selecting a pet should make it visible when the current
                // profile permits pets; otherwise the menu remains usable.
                if (PerformanceProfileManager.isPetAllowed()) {
                    PotassiumConfig.miniPetEnabled = true;
                }
            }
        } else {
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
                case 4:
                    Minecraft.getMinecraft().displayGuiScreen(
                            new PotassiumSettingsScreen(this));
                    return;
                case 5:
                    Minecraft.getMinecraft().displayGuiScreen(parent);
                    return;
                case 7:
                    PotassiumPetPackManager.reload();
                    status = "Pet packs refreshed.";
                    break;
                default:
                    return;
            }
        }

        save();
        initGui();
    }

    private int cycle(int value, int min, int max, int step) {
        int next = value + step;
        return next > max ? min : next;
    }

    private String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }

    private void save() {
        if (PotassiumConfig.getConfiguration() != null) {
            PotassiumConfig.getConfiguration().save();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int center = width / 2;
        int top = Math.max(42, height / 2 - 95);
        int gridTop = top + 30;

        // Inventory-style panel.
        drawRect(center - 210, top, center + 210, gridTop + 3 * 28 + 88, 0xB0101010);
        drawRect(center - 202, top + 8, center + 202, top + 28, 0xC01B1B1B);

        drawCenteredString(fontRendererObj, "Potassium Pets", center, top + 13, 0xFFFFFF);
        drawCenteredString(fontRendererObj,
                "Choose a cosmetic companion",
                center, top + 23, 0xAAAAAA);

        drawCenteredString(fontRendererObj,
                "20 built-in + imported pets • client-only",
                center, gridTop + 3 * 28 + 56, 0x777777);

        if (!PerformanceProfileManager.isPetAllowed()) {
            drawCenteredString(fontRendererObj,
                    "Mini Pet is disabled in this performance profile.",
                    center, gridTop + 3 * 28 + 68, 0xAAAAAA);
        } else if (!status.isEmpty()) {
            drawCenteredString(fontRendererObj, status,
                    center, gridTop + 3 * 28 + 68, 0xAAAAAA);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
