package com.predex.potassium.optimization;

import com.predex.potassium.config.PotassiumConfig;

public final class PerformanceManager {
    private PerformanceManager() {}

    public static boolean isEnabled() {
        return PotassiumConfig.enabled;
    }

    public static boolean isLowMemoryMode() {
        return PotassiumConfig.lowMemoryMode;
    }
}
