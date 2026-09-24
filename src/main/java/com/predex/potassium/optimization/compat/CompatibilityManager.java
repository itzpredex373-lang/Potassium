package com.predex.potassium.optimization.compat;

import net.minecraftforge.fml.common.Loader;

public final class CompatibilityManager {
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
}