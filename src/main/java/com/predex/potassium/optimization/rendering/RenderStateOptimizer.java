package com.predex.potassium.optimization.rendering;

public final class RenderStateOptimizer {
    private RenderStateOptimizer() {}

    public static boolean shouldUseFastPath(boolean translucent, boolean hasAnimation) {
        return !translucent && !hasAnimation;
    }

    public static boolean canReuseState(int previousTexture, int currentTexture,
                                        int previousBlendMode, int currentBlendMode) {
        return previousTexture == currentTexture
                && previousBlendMode == currentBlendMode;
    }

    public static int normalizeRenderPass(int pass) {
        return pass < 0 ? 0 : pass;
    }
}
