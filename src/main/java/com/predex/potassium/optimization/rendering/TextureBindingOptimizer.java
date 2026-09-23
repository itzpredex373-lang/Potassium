package com.predex.potassium.optimization.rendering;

public final class TextureBindingOptimizer {
    private int lastTexture = Integer.MIN_VALUE;

    public boolean shouldBind(int textureId) {
        if (lastTexture == textureId) return false;
        lastTexture = textureId;
        return true;
    }

    public void invalidate() {
        lastTexture = Integer.MIN_VALUE;
    }

    public int getLastTexture() { return lastTexture; }
}