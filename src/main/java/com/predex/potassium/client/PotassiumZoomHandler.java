package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

/**
 * Lightweight client-only zoom. Holding C smoothly narrows the FOV.
 * No server state, packets or player attributes are changed.
 */
public final class PotassiumZoomHandler {
    public static final KeyBinding ZOOM_KEY = new KeyBinding(
            "key.potassium.zoom", Keyboard.KEY_C, "key.categories.potassium");

    private float zoom = 1.0F;

    public void register() {
        ClientRegistry.registerKeyBinding(ZOOM_KEY);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        boolean wanted = PotassiumConfig.zoomEnabled
                && Minecraft.getMinecraft().thePlayer != null
                && ZOOM_KEY.isKeyDown();

        float target = wanted ? Math.max(0.10F,
                Math.min(0.60F, PotassiumConfig.zoomFovPercent / 100.0F)) : 1.0F;
        float speed = Math.max(1.0F, Math.min(20.0F, PotassiumConfig.zoomSmoothness)) * 0.08F;

        zoom += (target - zoom) * Math.min(1.0F, speed);
        if (Math.abs(target - zoom) < 0.002F) zoom = target;
    }

    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent event) {
        if (!PotassiumConfig.zoomEnabled || event.entity == null) return;
        event.newfov = event.newfov * zoom;
    }
}
