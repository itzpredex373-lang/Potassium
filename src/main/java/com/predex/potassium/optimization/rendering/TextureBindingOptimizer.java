package com.predex.potassium.optimization.rendering;

import java.util.HashSet;
import java.util.Set;

public final class TextureBindingOptimizer {
    private static final Set<Integer> visibleTextures = new HashSet<Integer>();
    private TextureBindingOptimizer() {}
    public static void beginFrame() { visibleTextures.clear(); }
    public static void markVisible(int textureId) {
        if (textureId >= 0) visibleTextures.add(Integer.valueOf(textureId));
    }
    public static boolean wasVisible(int textureId) {
        return visibleTextures.contains(Integer.valueOf(textureId));
    }
    public static int visibleTextureCount() { return visibleTextures.size(); }
}
