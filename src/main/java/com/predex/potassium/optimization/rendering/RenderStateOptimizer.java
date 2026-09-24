package com.predex.potassium.optimization.rendering;

import java.util.HashMap;
import java.util.Map;

public final class RenderStateOptimizer {
    private static final Map<String, Boolean> cache = new HashMap<String, Boolean>();
    private RenderStateOptimizer() {}
    public static void beginFrame() { cache.clear(); }
    public static boolean shouldApply(String state, boolean enabled) {
        if (state == null) return true;
        Boolean old = cache.get(state);
        if (old != null && old.booleanValue() == enabled) return false;
        cache.put(state, Boolean.valueOf(enabled));
        return true;
    }
}
