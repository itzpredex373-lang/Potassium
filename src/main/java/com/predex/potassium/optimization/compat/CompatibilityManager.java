package com.predex.potassium.optimization.compat;

import net.minecraftforge.fml.common.Loader;

public final class CompatibilityManager {
    private CompatibilityManager() {}

    public static boolean isForgePresent() { return true; }

    public static boolean isOptiFinePresent() {
        try {
            Class.forName("optifine.Installer", false, CompatibilityManager.class.getClassLoader());
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
}