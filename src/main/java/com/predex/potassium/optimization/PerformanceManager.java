package com.predex.potassium.optimization;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Central, allocation-free access point for Potassium performance decisions.
 */
public final class PerformanceManager {
    private static long clientTickCount;
    private static boolean optimizationsEnabled = true;

    private PerformanceManager() {}

    public static boolean isEnabled() {
        return PotassiumConfig.enabled;
    }

    public static boolean isOptimizationEnabled() {
        return PotassiumConfig.enabled && optimizationsEnabled;
    }

    public static void setOptimizationsEnabled(boolean enabled) {
        optimizationsEnabled = enabled;
    }

    public static boolean isLowMemoryMode() {
        return isOptimizationEnabled() && PotassiumConfig.lowMemoryMode;
    }

    public static boolean shouldReduceParticles() {
        return isOptimizationEnabled() && PotassiumConfig.reduceParticles;
    }

    public static boolean shouldReduceEntityUpdates() {
        return isOptimizationEnabled() && PotassiumConfig.reduceEntityUpdates;
    }

    public static int getEntityRenderDistance() {
        return PotassiumConfig.entityRenderDistance;
    }

    public static void onClientTick() {
        clientTickCount++;
    }

    public static long getClientTickCount() {
        return clientTickCount;
    }

    public static boolean isMaintenanceTick() {
        return (clientTickCount & 31L) == 0L;
    }
}
