package com.predex.potassium.optimization.compat;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.fml.common.Loader;

public final class CompatibilityManager {
    private static volatile int rendererFailures;
    private static volatile boolean customRendererDisabled;

    private CompatibilityManager() {}

    public static boolean isForgePresent() { return true; }

    public static boolean isOptiFinePresent() {
        try {
            ClassLoader loader = CompatibilityManager.class.getClassLoader();
            Class.forName("optifine.OptiFineForgeTweaker", false, loader);
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static boolean isLoaded(String modId) {
        return modId != null && Loader.isModLoaded(modId);
    }

    public static boolean allowRiskyHooks() {
        return !isOptiFinePresent();
    }

    /**
     * Custom VBO ownership/draw submission is intentionally stricter than
     * ordinary Potassium hooks. OptiFine and unsupported VBO hardware get a
     * vanilla path instead of attempting to share GPU state.
     */
    public static boolean allowCustomRenderer() {
        if (!PotassiumConfig.customGpuRenderer || !PotassiumConfig.customDrawSubmission) {
            return false;
        }
        if (customRendererDisabled || isOptiFinePresent()) {
            return false;
        }

        try {
            if (!OpenGlHelper.vboSupported) {
                return false;
            }

            Minecraft mc = Minecraft.getMinecraft();
            return mc != null && mc.gameSettings != null && mc.gameSettings.useVbo;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void recordRendererFailure() {
        int failures = ++rendererFailures;
        if (failures >= 3) {
            customRendererDisabled = true;
        }
    }

    public static int getRendererFailures() {
        return rendererFailures;
    }

    public static boolean isCustomRendererDisabled() {
        return customRendererDisabled;
    }

    public static void resetRendererHealth() {
        rendererFailures = 0;
        customRendererDisabled = false;
    }
}