package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Entry point for client rendering optimizations.
 *
 * This class intentionally keeps the first rendering milestone conservative.
 * Actual renderer hooks will be added incrementally after benchmarking.
 */
public final class RenderOptimizer {
    private RenderOptimizer() {}

    public static boolean isEnabled() {
        return PotassiumConfig.enabled;
    }

    public static boolean isLowMemoryMode() {
        return PotassiumConfig.lowMemoryMode;
    }
}
