package com.predex.potassium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public final class PotassiumPetKeyHandler {
    public static final KeyBinding PET_MENU_KEY = new KeyBinding(
            "key.potassium.pet_menu", Keyboard.KEY_P, "key.categories.potassium");

    private boolean registered;

    public void register() {
        ClientRegistry.registerKeyBinding(PET_MENU_KEY);
        ClientCommandHandler.instance.registerCommand(new PotassiumPetCommand());
        registered = true;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (registered && PET_MENU_KEY.isPressed()
                && Minecraft.getMinecraft().theWorld != null) {
            PotassiumPetMenuScreen.open();
        }
    }
}