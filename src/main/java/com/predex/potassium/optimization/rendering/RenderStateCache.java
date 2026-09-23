package com.predex.potassium.optimization.rendering;

public final class RenderStateCache {
    private int texture = Integer.MIN_VALUE;
    private int blend = Integer.MIN_VALUE;
    private int bound = Integer.MIN_VALUE;

    public boolean textureChanged(int value) {
        if (texture == value) return false;
        texture = value;
        return true;
    }

    public boolean blendChanged(int value) {
        if (blend == value) return false;
        blend = value;
        return true;
    }

    public boolean bindingChanged(int value) {
        if (bound == value) return false;
        bound = value;
        return true;
    }

    public void invalidate() {
        texture = blend = bound = Integer.MIN_VALUE;
    }
}