package com.predex.potassium.optimization;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Central, allocation-free access point for Potassium performance decisions.
 */
public final class PerformanceManager {
    private static long clientTickCount;

    private PerformanceManager() {}

    public static boolean isEnabled() {
        return PotassiumConfig.enabled;
    }

    public static boolean isLowMemoryMode() {
        return PotassiumConfig.lowMemoryMode;
    }

    public static boolean shouldReduceParticles() {
        return isEnabled() && PotassiumConfig.reduceParticles;
    }

    public static boolean shouldReduceEntityUpdates() {
        return isEnabled() && PotassiumConfig.reduceEntityUpdates;
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

    /**
     * Small periodic-task gate. It avoids doing optional maintenance every tick.
     */
    public static boolean isMaintenanceTick() {
        return (clientTickCount & 31L) == 0L;
    }
}
